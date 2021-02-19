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
    // 方法名称
    private String methodName;

    public PackageClassVisitor(ClassVisitor cv) {
        super(ASM6, cv);
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


        final Type[] argumentTypes = Type.getArgumentTypes(desc);



        // System.out.println("method:" + name);
        // init 方法调用对象的构造方法，会触发init方法
        // cinit 在类初始化阶段，会触发cinit方法
        this.methodName = name;
        if (!name.equals("<init>") && mv != null) {
            mv = new MyMethodVisitor(mv);
        }
        // 静态初始化方法
        if (name.equals("<clinit>") && mv != null) {
            mv = new CinitMethodVisit(mv, this.className);
        }



        return mv;
    }




    class MyMethodVisitor extends MethodVisitor implements Opcodes {

        public MyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv);
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
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

        @Override
        public void visitParameter(String name, int access) {
            super.visitParameter(name, access);
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            super.visitAttribute(attribute);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }




        @Override
        public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            super.visitAnnotableParameterCount(parameterCount, visible);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {

            System.out.println(name);


            super.visitLocalVariable(name, descriptor, signature, start, end, index);
        }

        @Override
        public void visitCode() {
            // AnnotationVisitor annotationVisitor = this.visitAnnotation("Lfun/gengzi/boot/instrument/annotations/BaseLog;", true);
            if (isAnnotationPresent) {
                System.out.println("Baselog注解增强的方法method:" + methodName);
                // 增加日志加入的代码

                // 增加service 拦截的代码



                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitFieldInsn(GETSTATIC, className, "glog1024", "Lorg/slf4j/Logger;");
                mv.visitLdcInsn(loginfo);
                mv.visitMethodInsn(INVOKEINTERFACE, "org/slf4j/Logger", "info", "(Ljava/lang/String;)V", true);
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