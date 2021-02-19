package fun.gengzi.core.myclass;

import fun.gengzi.core.GlogBootstrap;
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
import static org.objectweb.asm.Opcodes.ACC_ENUM;

/**
 * <h1>类字节码插桩工厂</h1>
 * <p>
 * <p>
 * 作用：过滤需要进行插桩的类
 *
 *
 * https://www.cnblogs.com/hebaibai/archive/2019/06/12/11011004.html
 *
 * @author gengzi
 * @date 2021年2月8日13:48:36
 */
public class ClassInstrumentationFactory {

    private final static Object ENTRY = new Object();
    // 记录已经转换过的类
    private final static ConcurrentMap<String, Object> MODIFY_CLASS_MAP = new ConcurrentHashMap();

    /**
     * 修改类
     *
     * @return
     */
    public static byte[] modifyTheClass(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IOException {
        // 校验包名
        boolean flag = packageNameDetection(className);
        if (!flag) {
            return classfileBuffer;
        }
        // 校验类的类型
        ClassReader classReader = new ClassReader(classfileBuffer);
        // 过滤接口
        if ((classReader.getAccess() & ACC_INTERFACE) == ACC_INTERFACE) {
            return classfileBuffer;
        }

        if ((classReader.getAccess() & ACC_ABSTRACT) == ACC_ABSTRACT) {
            return classfileBuffer;
        }

        // 过滤注解
        if ((classReader.getAccess() & ACC_ANNOTATION) == ACC_ANNOTATION) {
            return classfileBuffer;
        }
        // 过滤枚举类
        if ((classReader.getAccess() & ACC_ENUM) == ACC_ENUM) {
            return classfileBuffer;
        }
        String supperName = classReader.getSuperName();
        // 过滤异常类
        if ("java/lang/RuntimeException".equals(supperName)
                || "java/lang/Exception".equals(supperName)
                || "java/lang/Throwable".equals(supperName)) {
            return null;
        }

        // 过滤内部类
        if(className.contains("$1")){
            return classfileBuffer;
        }

        // 判断是否已经转换的类
        if (MODIFY_CLASS_MAP.containsKey(classReader.getClassName())) {
            return classfileBuffer;
        }

        try {
            System.out.println("classname" + className);
            // 准备去转换
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
                @Override
                protected ClassLoader getClassLoader() {
                    return loader;
                }
            };
            //处理
            ClassVisitor classVisitor = new PackageClassVisitor(classWriter);
            classReader.accept(classVisitor, ClassReader.SKIP_FRAMES);
            byte[] data = classWriter.toByteArray();
//            // 转换成功，保存class文件
////            ByteCodeUtils.savaToFile(classReader.getClassName(), data);
//            FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\ideaworkspace\\BootStrapTest.class"));
//            fileOutputStream.write(data);
//            fileOutputStream.close();
            MODIFY_CLASS_MAP.put(classReader.getClassName(), ENTRY);
            return data;
        } catch (Throwable throwable) {
            MODIFY_CLASS_MAP.remove(classReader.getClassName());
            throw throwable;
        }

    }


    /**
     * 包名检测
     *
     * @param className
     * @return
     */
    public static boolean packageNameDetection(String className) {
        if (className == null || "".equals(className)) {
            return false;

        }
        // 仅匹配设置的包名，并判断包名不能为特殊包名下的类
        String packageName = GlogBootstrap.agentArgs;

        if (className != null && (className.startsWith("sun/") || className.startsWith("java/"))) {
            // 不加载
            return false;
        }
        // 替换包名
        className = className.replace("/", ".");

        if (!"".equals(packageName) && className.startsWith(packageName)) {
            return true;
        }

        return false;
    }


}
