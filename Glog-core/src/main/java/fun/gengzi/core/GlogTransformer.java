package fun.gengzi.core;

import fun.gengzi.core.myclass.ClassInstrumentationFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * baselog 类文件转换器
 */
public class GlogTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
//            System.out.println(className);
            if ("java/util/concurrent/ThreadPoolExecutor".equals(className)) {
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
                // TODO 测试方法
                FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\ideaworkspace\\ThreadPoolExecutor.class"));
                fileOutputStream.write(data);
                fileOutputStream.close();
                return data;
            }
//            else if (isAppClassloader(loader, className)) {
//                // 修改测试代码的字节码
//                //读取
//                ClassReader classReader = new ClassReader("java.util.concurrent.ThreadPoolExecutor");
//                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS) {
//                    @Override
//                    protected ClassLoader getClassLoader() {
//                        return loader;
//                    }
//                };
//                //处理
//                ClassVisitor classVisitor = new GlogClassVisitor(classWriter);
//                classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
//                byte[] data = classWriter.toByteArray();
//                return data;
//            }

            byte[] bytes = ClassInstrumentationFactory.modifyTheClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isAppClassloader(ClassLoader loader, String className) {
        // 排除java 包，自身包，只寻找源码包
        // TODO 使用字节码增强技术解决，spring aop 方法嵌套的问题

        // 利用java 探针技术，修改jdk 源码，反射技术，创建对象



        if ("fun/gengzi/test/BootStrapTest".equals(className)) {
            return true;
        }
        return false;
    }

}