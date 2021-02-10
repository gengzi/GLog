package fun.gengzi.core.myclass;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <h1>静态初始化方法转换事件</h1>
 * <p>
 * 作用： 处理<cinit> 的方法，为logger 实例字段指定引用的对象
 *
 * @author gengzi
 * @date 2021年2月10日13:42:17
 */
public class CinitMethodVisit extends MethodVisitor implements Opcodes {
    private String className;

    public CinitMethodVisit(MethodVisitor methodVisitor, String className) {
        super(Opcodes.ASM5, methodVisitor);
        this.className = className;
    }

    @Override
    public void visitCode() {
        // 追加logger 字段
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLdcInsn(Type.getType(String.format("L%s;", className)));
        mv.visitMethodInsn(INVOKESTATIC, "org/slf4j/LoggerFactory", "getLogger", "(Ljava/lang/Class;)Lorg/slf4j/Logger;", false);
        mv.visitFieldInsn(PUTSTATIC, className, "glog1024", "Lorg/slf4j/Logger;");
        mv.visitEnd();
        super.visitCode();
    }
}
