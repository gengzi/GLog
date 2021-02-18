package fun.gengzi.core;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.CodeSource;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;

/**
 * <h1>核保包-引导入口</h1>
 * <p>
 * 由agent 触发，用于加载Base中的植入代码。
 * 追加自定义的类转换器，实现字节码增强
 *
 * @author gengzi
 * @date 2021年2月18日14:38:10
 */
public class GlogBootstrap {
    // 基础jar
    private static final String GOLG_BASE_JAR = "Glog-Base.jar";

    private static GlogBootstrap arthasBootstrap;

    private AtomicBoolean isBindRef = new AtomicBoolean(false);
    private Instrumentation instrumentation;

    // 参数
    public static String agentArgs = null;

    private GlogBootstrap(Instrumentation instrumentation) throws Throwable {
        this.instrumentation = instrumentation;
        // 获取父类加载器
        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        Class<?> spyClass = null;
        if (parent != null) {
            try {
                spyClass = parent.loadClass("java.glog.base.MDCInheritableThreadLocal");
            } catch (Throwable e) {
                // ignore
            }
        }
        // 加载失败，就将base jar 追加到 BootstrapClassLoader 中加载
        if (spyClass == null) {
            CodeSource codeSource = GlogBootstrap.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File glogCoreJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                File baseJarFile = new File(glogCoreJarFile.getParentFile(), GOLG_BASE_JAR);
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(baseJarFile));
            } else {
                throw new IllegalStateException("can not find " + GOLG_BASE_JAR);
            }
        }

        GlogTransformer glogTransformer = new GlogTransformer();
        // 加载自定义的ClassFileTransformer
        instrumentation.addTransformer((ClassFileTransformer) glogTransformer, true);
        // 重定义类并载入新的字节码
        // instrumentation.retransformClasses(ThreadPoolExecutor.class);
    }


    /**
     * 单例，获取GlogBootstrap 的实例
     *
     * @param instrumentation JVM增强
     * @return GlogBootstrap单例
     * @throws Throwable
     */
    public synchronized static GlogBootstrap getInstance(Instrumentation instrumentation, String args) throws Throwable {
        if (arthasBootstrap != null) {
            return arthasBootstrap;
        }
        int index = args.indexOf(';');
        // 从命令行设置要字节码编码的包名
        if (index != -1) {
            agentArgs = args.substring(index + 1);
        } else {
            agentArgs = args;
        }
        return getInstance(instrumentation);
    }

    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @return ArthasServer单例
     * @throws Throwable
     */
    public synchronized static GlogBootstrap getInstance(Instrumentation instrumentation) throws Throwable {
        if (arthasBootstrap == null) {
            arthasBootstrap = new GlogBootstrap(instrumentation);
        }
        return arthasBootstrap;
    }


    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }


}
