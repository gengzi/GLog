package fun.gengzi.core;

import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Constructor;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;


public class GlogBootstrap {
    private static final String GOLG_BASE_JAR = "Glog-Base.jar";


    private static GlogBootstrap arthasBootstrap;

    private AtomicBoolean isBindRef = new AtomicBoolean(false);
    private Instrumentation instrumentation;

    private GlogBootstrap(Instrumentation instrumentation) throws Throwable {
        this.instrumentation = instrumentation;
        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        Class<?> spyClass = null;
        if (parent != null) {
            try {
                spyClass = parent.loadClass("java.glog.base.MDCInheritableThreadLocal");
            } catch (Throwable e) {
                // ignore
            }
        }
        if (spyClass == null) {
            CodeSource codeSource = GlogBootstrap.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File arthasCoreJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                File spyJarFile = new File(arthasCoreJarFile.getParentFile(), GOLG_BASE_JAR);
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
//                Class<?> aClass = parent.loadClass("java.glog.base.MDCInheritableThreadLocal");
//                Constructor<?> constructor = aClass.getConstructor();
//                Object o = constructor.newInstance();
            } else {
                throw new IllegalStateException("can not find " + GOLG_BASE_JAR);
            }
        }
        GlogTransformer glogTransformer = new GlogTransformer();
        // 加载自定义的ClassFileTransformer
        instrumentation.addTransformer((ClassFileTransformer) glogTransformer, true);
        // 重定义类并载入新的字节码
        instrumentation.retransformClasses(ThreadPoolExecutor.class);
    }


    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @return ArthasServer单例
     * @throws Throwable
     */
    public synchronized static GlogBootstrap getInstance(Instrumentation instrumentation, String args) throws Throwable {
        if (arthasBootstrap != null) {
            return arthasBootstrap;
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
