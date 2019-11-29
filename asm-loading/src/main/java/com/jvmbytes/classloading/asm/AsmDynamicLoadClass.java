package com.jvmbytes.classloading.asm;


import com.jvmbytes.classloading.util.ClassloaderReleaseUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.PrintStream;

/**
 * @author wongoo
 */
public class AsmDynamicLoadClass {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            if (i > 0) {
                Thread.sleep(1000 * 10);
            }
            dynamicLoad("TestDynamicClass_" + i, 100);
        }
    }

    private static void dynamicLoad(String prefix, int size) throws Exception {
        TestClassLoader classLoader = new TestClassLoader();
        for (int i = 0; i < size; i++) {
            dynamicCreateClazz(classLoader, prefix + "_" + i);
            Thread.sleep(1);
        }

        ClassloaderReleaseUtil.closeIfPossible(classLoader);
    }

    private static Class dynamicCreateClazz(TestClassLoader classLoader, String clazzName) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(49, Opcodes.ACC_PUBLIC, clazzName, null, "java/lang/Object", null);

        Method m = Method.getMethod("void <init> ()");
        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        ga.loadThis();
        ga.invokeConstructor(Type.getType(Object.class), m);
        ga.returnValue();

        // generate doSomething method
        m = Method.getMethod("void doSomething()");
        ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        ga.getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
        ga.push("dynamic generated class");
        ga.invokeVirtual(Type.getType(PrintStream.class), Method.getMethod("void println(String)"));
        ga.returnValue();
        ga.endMethod();

        byte[] bytes = cw.toByteArray();
        return classLoader.defineClassByName(clazzName, bytes, 0, bytes.length);
    }

    public static class TestClassLoader extends ClassLoader {

        public Class defineClassByName(String name, byte[] b, int off, int len) {
            Class clazz = super.defineClass(name, b, off, len);
            return clazz;
        }
    }
}
