package fun.gengzi.core;

import fun.gengzi.core.jdk.GlogClassVisitor;
import fun.gengzi.core.jdk.JdkClassInstrumentationFactory;
import fun.gengzi.core.myclass.ClassInstrumentationFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * <h1>baselog 类文件转换器</h1>
 *
 * @author gengzi
 * @date 2021年2月18日14:46:03
 */
public class GlogTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            byte[] bytes;
            // 对于jdk 源码的改造
            if ("java/util/concurrent/ThreadPoolExecutor".equals(className)) {
                bytes = JdkClassInstrumentationFactory.modifyTheClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            } else {
                // 对于本地工程的源码改造
                bytes = ClassInstrumentationFactory.modifyTheClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            }
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}