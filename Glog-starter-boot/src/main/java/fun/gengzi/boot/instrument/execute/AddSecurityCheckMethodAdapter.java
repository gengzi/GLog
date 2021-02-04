package fun.gengzi.boot.instrument.execute;//package fun.gengzi.baselog.instrument.execute;
//
//import com.sun.xml.internal.ws.org.objectweb.asm.MethodAdapter;
//import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
//import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;
//
//public class AddSecurityCheckMethodAdapter extends MethodAdapter {
//	 public AddSecurityCheckMethodAdapter(MethodVisitor mv) {
//		 super(mv);
//	 }
//
//	 @Override
//	 public void visitCode() {
//		 visitMethodInsn(Opcodes.INVOKESTATIC, "MDCInheritableThreadLocal",
//			"setMdc", "()V");
//	 }
// }