package fun.gengzi.core.myclass;

import org.objectweb.asm.*;


/**
 * <h1>类转换器</h1>
 * <p>
 * <p>
 * 将类
 *
 * @author gengzi
 * @date 2021年2月9日10:39:11
 */
public class PackageClassVisitor extends ClassVisitor implements Opcodes {

    // 日志打印内容
    private String loginfo = "";
    // 是否存在改注释
    private boolean isAnnotationPresent;
    // 是否存在logger类
    private boolean loggerFlag;
    // 参数名称
    private String className;

    public PackageClassVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        // 检测当前类是否存在 Logger 类型的字段，以便于打印日志，如果不存在，就追加该字段
        if ("Lorg/slf4j/Logger".equals(descriptor)) {
            loggerFlag = true;
        }
        return cv.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.className = name;
        cv.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        if (!loggerFlag) {
            FieldVisitor fv = cv.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "glog1024", "Lorg/slf4j/Logger;", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions);
        System.out.println("method:"+name);
        //Base类中有两个方法：无参构造以及process方法，这里不增强构造方法
        if (!name.equals("<init>") && mv != null) {
            mv = new MyMethodVisitor(mv);
        }
        // 静态初始化方法
        if (name.equals("<clinit>") && mv != null) {
            mv = new CinitMethodVisit(mv, this.className);
        }

        // 过滤对应的方法

        return mv;
    }


    class MyMethodVisitor extends MethodVisitor implements Opcodes {

        public MyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        /**
         * 加载注释
         *
         * @param descriptor
         * @param visible
         * @return
         */
        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // Lfun/gengzi/boot/instrument/annotations/BaseLog;
            // 执行自定义的注释转换事件
            return new MyAnnotationVisitor(Opcodes.ASM5, super.visitAnnotation(descriptor, visible));
        }

        @Override
        public void visitCode() {
            // AnnotationVisitor annotationVisitor = this.visitAnnotation("Lfun/gengzi/boot/instrument/annotations/BaseLog;", true);
            if (isAnnotationPresent) {
                // 增加日志加入的代码
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitFieldInsn(GETSTATIC, className, "glog1024", "Lorg/slf4j/Logger;");
                mv.visitLdcInsn(loginfo);
                mv.visitMethodInsn(INVOKEINTERFACE, "org/slf4j/Logger", "info", "(Ljava/lang/String;)V", true);

                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(loginfo);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                mv.visitEnd();
            }
            // 重置临时变量的值
            isAnnotationPresent = false;
            loginfo = "";
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            mv.visitInsn(opcode);
        }
    }


    class MyAnnotationVisitor extends AnnotationVisitor implements Opcodes {

        public MyAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            if ("businessInfo".equals(name)) {
                isAnnotationPresent = true;
                loginfo = (String) value;
            }
            super.visit(name, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            return super.visitAnnotation(name, descriptor);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            super.visitEnum(name, descriptor, value);
        }
    }


}