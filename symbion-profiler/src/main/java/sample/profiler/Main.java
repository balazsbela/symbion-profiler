package sample.profiler;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;


public class Main {

	public static void premain(String args, Instrumentation inst) {
		inst.addTransformer(new Transformer());
	}
}

class Transformer implements ClassFileTransformer {
	
	public byte[] transform(ClassLoader loader, String className, 
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain, 
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		// can only profile classes that will be able to see
		// the Profile class which is loaded by the application
		// classloader
		//
		if (loader != ClassLoader.getSystemClassLoader()) {
			return classfileBuffer;
		}
		
		// can't profile yourself
		//
		if (className.startsWith("sample/profiler")) {
			return classfileBuffer;
		}		
		
		byte[] result = classfileBuffer;
		ClassReader reader = new ClassReader(classfileBuffer);
		ClassWriter writer = new ClassWriter(true);
		ClassAdapter adapter = new PerfClassAdapter(writer, className);
		reader.accept(adapter, true);
		result = writer.toByteArray();
		return result;
	}
}