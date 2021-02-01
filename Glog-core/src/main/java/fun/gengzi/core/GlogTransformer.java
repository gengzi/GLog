package fun.gengzi.core;

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
            System.out.println(className);
            if (className.equals("java/util/concurrent/ThreadPoolExecutor")) {
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
                // 测试方法
                FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\ideaworkspace\\ThreadPoolExecutor.class"));
                fileOutputStream.write(data);
                fileOutputStream.close();
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}