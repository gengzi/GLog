package fun.gengzi.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <h1>appClassLoader 类修改事件</h1>
 */
public class GlogAppClassLoaderClassVisitor extends ClassVisitor implements Opcodes {

    public GlogAppClassLoaderClassVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(V1_5, access, name, signature, superName, interfaces);
    }


    




}