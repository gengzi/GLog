package fun.gengzi.glog.agent;


import fun.gengzi.glog.classloader.GlogClassLoader;

import java.glog.base.MDCInheritableThreadLocal;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.jar.JarFile;

/**
 * <h1>java 代理类</h1>
 * <p>
 * 启用方式
 * -javaagent:<jarpath>[=<选项>]
 * 加载 Java 编程语言代理, 请参阅 java.lang.instrument
 *
 * @author gengzi
 * @date 2021年1月27日20:45:26
 */
final class LogAgent {

    private static final String GOLG_CORE_JAR = "Glog-core.jar";
    private static final String GOLG_BASE_JAR = "Glog-Base.jar";
    private static final String GOLG_TRANSFORMER = "fun.gengzi.core.GlogTransformer";
    private static PrintStream ps = System.err;
    /**
     * <pre>
     * 1. 全局持有classloader用于隔离 Glog 实现
     * 2. ClassLoader在Glog停止时会被reset
     * 3. 如果ClassLoader一直没变，则 com.taobao.arthas.core.server.ArthasBootstrap#getInstance 返回结果一直是一样的
     * </pre>
     */
    private static volatile ClassLoader glogClassLoader;

    /**
     * 让下次再次启动时有机会重新加载
     */
    public static void resetGlogClassLoader() {
        glogClassLoader = null;
    }


    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Throwable {
        runLauncher(agentArgs, instrumentation);
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) throws Throwable {
        runLauncher(agentArgs, instrumentation);
    }


    /**
     * 同步方法
     *
     * @param args
     * @param instrumentation
     * @throws Throwable
     */
    private static synchronized void runLauncher(String args, Instrumentation instrumentation) {
        try {
            ps.println("<<< LogAgent start >>>");
            try {
                Class.forName("java.glog.base.MDCInheritableThreadLocal"); // 加载不到会抛异常
            } catch (Throwable e) {
            }
            // 传递的args参数分两个部分:arthasCoreJar路径和agentArgs, 分别是Agent的JAR包路径和期望传递到服务端的参数
            if (args == null) {
                args = "";
            }
            args = decodeArg(args);

            String glogCoreJar;
            final String agentArgs;
            int index = args.indexOf(';');
            if (index != -1) {
                glogCoreJar = args.substring(0, index);
                agentArgs = args.substring(index);
            } else {
                glogCoreJar = "";
                agentArgs = args;
            }
            File glogCoreJarFile = new File(glogCoreJar);

            if (!glogCoreJarFile.exists()) {
                ps.println("Can not find glog-core jar file from args: " + glogCoreJarFile);
                // try to find from arthas-agent.jar directory
                CodeSource codeSource = LogAgent.class.getProtectionDomain().getCodeSource();
                if (codeSource != null) {
                    try {
                        File glogAgentJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                        glogCoreJarFile = new File(glogAgentJarFile.getParentFile(), GOLG_CORE_JAR);
                        if (!glogCoreJarFile.exists()) {
                            ps.println("Can not find glog-core jar file from agent jar directory: " + glogCoreJarFile);
                        }
                    } catch (Throwable e) {
                        ps.println("Can not find glog-core jar file from " + codeSource.getLocation());
                        e.printStackTrace(ps);
                    }
                }
            }
            if (!glogCoreJarFile.exists()) {
                return;
            }


            ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
            Class<?> spyClass = null;
            if (parent != null) {
                try {
                    spyClass =parent.loadClass("java.glog.base.MDCInheritableThreadLocal");
                } catch (Throwable e) {
                    // ignore
                }
            }
            if (spyClass == null) {
                CodeSource codeSource = LogAgent.class.getProtectionDomain().getCodeSource();
                if (codeSource != null) {
                    File arthasCoreJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                    File spyJarFile = new File(arthasCoreJarFile.getParentFile(), GOLG_BASE_JAR);
                    instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
                    Class<?> aClass = parent.loadClass("java.glog.base.MDCInheritableThreadLocal");
                    Constructor<?> constructor = aClass.getConstructor();
                    Object o = constructor.newInstance();
                } else {
                    throw new IllegalStateException("can not find " + GOLG_BASE_JAR);
                }
            }

//            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile("D:\\ideaworkspace\\Glog-Base.jar"));
            final ClassLoader agentLoader = getClassLoader(instrumentation, glogCoreJarFile);
            Class<?> aClass = agentLoader.loadClass(GOLG_TRANSFORMER);
            Constructor<?> constructor = aClass.getConstructor();
            Object instance = constructor.newInstance();
            // 加载 自定义的ClassFileTransformer
            instrumentation.addTransformer((ClassFileTransformer) instance, true);
            System.out.println("Agent Load Done.");

            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();

            for (int i = 0; i < allLoadedClasses.length; i++) {
                System.out.println(allLoadedClasses[i]);
            }


        } catch (Throwable t) {
            t.printStackTrace(ps);
            try {
                if (ps != System.err) {
                    ps.close();
                }
            } catch (Throwable tt) {
                // ignore
            }
            throw new RuntimeException(t);
        }

    }


    private static ClassLoader getClassLoader(Instrumentation inst, File arthasCoreJarFile) throws Throwable {
        // 构造自定义的类加载器，尽量减少Arthas对现有工程的侵蚀
        return loadOrDefineClassLoader(arthasCoreJarFile);
    }


    private static ClassLoader loadOrDefineClassLoader(File glogCoreJarFile) throws Throwable {
        if (glogClassLoader == null) {
            glogClassLoader = new GlogClassLoader(new URL[]{glogCoreJarFile.toURI().toURL()});
        }
        return glogClassLoader;
    }


    private static String decodeArg(String arg) {
        try {
            return URLDecoder.decode(arg, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return arg;
        }
    }

}
