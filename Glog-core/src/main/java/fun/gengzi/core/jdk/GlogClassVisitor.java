package fun.gengzi.core.jdk;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <h1>JDK类转换器</h1>
 *
 * @author gengzi
 * @date 2021年2月18日15:15:14
 */
public class GlogClassVisitor extends ClassVisitor implements Opcodes {
    public GlogClassVisitor(ClassVisitor cv) {
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
        // Base类中有两个方法：无参构造以及process方法，这里不增强构造方法
        // 只修改必要方法
        if (name.equals("execute") && mv != null) {
            mv = new MyMethodVisitor(mv);
        }
        return mv;
    }

    class MyMethodVisitor extends MethodVisitor implements Opcodes {
        public MyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }


        @Override
        public void visitCode() {
            super.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitMethodInsn(INVOKESTATIC, "java/glog/base/MDCInheritableThreadLocal", "get", "()Ljava/lang/Object;", false);
            Label label1 = new Label();
            mv.visitJumpInsn(IFNULL, label1);
            // TODO 移除关于slf4j 的内容
//            Label label2 = new Label();
//            mv.visitLabel(label2);
//            mv.visitMethodInsn(INVOKESTATIC, "java/glog/base/MDCInheritableThreadLocal", "get", "()Ljava/lang/Object;", false);
//            mv.visitTypeInsn(CHECKCAST, "java/util/Map");
//            mv.visitMethodInsn(INVOKESTATIC, "org/slf4j/MDC", "setContextMap", "(Ljava/util/Map;)V", false);
//            mv.visitLabel(label1);
        }

        @Override
        public void visitInsn(int opcode) {
            mv.visitInsn(opcode);
        }
    }
}