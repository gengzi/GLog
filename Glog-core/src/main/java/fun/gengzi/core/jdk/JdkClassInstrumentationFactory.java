package fun.gengzi.core.jdk;

import fun.gengzi.core.GlogBootstrap;
import fun.gengzi.core.myclass.PackageClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.objectweb.asm.Opcodes.*;

/**
 * <h1>jdk类字节码插桩工厂</h1>
 * <p>
 * <p>
 * 作用：过滤需要进行插桩的类
 *
 * @author gengzi
 * @date 2021年2月8日13:48:36
 */
public class JdkClassInstrumentationFactory {

    /**
     * 修改类
     *
     * @return
     */
    public static byte[] modifyTheClass(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IOException {
        // System.out.println(className);
        System.out.println("Transforming " + className);
        //读取
        ClassReader classReader = new ClassReader("java.util.concurrent.ThreadPoolExecutor");
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS) {
            @Override
            protected ClassLoader getClassLoader() {
                return loader;
            }
        };
        //处理
        ClassVisitor classVisitor = new GlogClassVisitor(classWriter);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
        byte[] data = classWriter.toByteArray();
//        // TODO 测试方法
//        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\ideaworkspace\\ThreadPoolExecutor.class"));
//        fileOutputStream.write(data);
//        fileOutputStream.close();
        return data;
    }


}
