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

    private String loginfo = "";
    private boolean isAnnotationPresent;

    public PackageClassVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions);

        //Base类中有两个方法：无参构造以及process方法，这里不增强构造方法
        if (!name.equals("<init>") && mv != null) {
            mv = new MyMethodVisitor(mv);
        }
        return mv;
    }

    class MyMethodVisitor extends MethodVisitor implements Opcodes {


        public MyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }


        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // Lfun/gengzi/boot/instrument/annotations/BaseLog;
            return new MyAnnotationVisitor(Opcodes.ASM5, super.visitAnnotation(descriptor, visible));
//            return super.visitAnnotation(descriptor, visible);
        }


        @Override
        public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            super.visitAnnotableParameterCount(parameterCount, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return super.visitAnnotationDefault();
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            super.visitAttribute(attribute);
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
        }


        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public void visitCode() {
//
//            AnnotationVisitor annotationVisitor = this.visitAnnotation("Lfun/gengzi/boot/instrument/annotations/BaseLog;", true);

            if (isAnnotationPresent) {
                // 增加日志加入的代码
                System.out.println(loginfo);
            }


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