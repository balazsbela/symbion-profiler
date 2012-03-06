package org.balazsbela.symbion.profiler;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.balazsbela.symbion.profiler.Log.print;

public class BytecodeTransformer {

	public static boolean enabled;

	public static byte[] transform(String className, ClassLoader loader, byte[] classBytes, Config config)
			throws IOException {
		if (!enabled) {
			return null;
		}

		try {
			byte[] bytes = transformMethodAsNeeded(classBytes, config);
			if (bytes != null) {
				Log.print(2, "Probed an CHANGED class " + className);

			} else {
				Log.print(2, "Probed class " + className);

			}
			if (bytes != null) {
				synchronized (Agent.modifiedClassNames) {
					if (Agent.modifiedClassNames.contains(className)) {
						Log.print(0, "Found duplicated class name " + className + " in loader "
								+ ((loader == null) ? "BootClassLoader" : loader.toString()));
					} else {
						Agent.modifiedClassNames.add(className);
					}
					// System.out.println("Agent.modifiedClassNames.size()="+Agent.modifiedClassNames.size());
				}
				return bytes;
			}
			return null;
		} finally {

		}
	}

	private static byte[] transformMethodAsNeeded(byte[] classBytes, Config config) {
		ClassReader cr = new ClassReader(classBytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		EnterExitClassAdapter ca = new EnterExitClassAdapter(cw, config);
		cr.accept(ca, org.objectweb.asm.ClassReader.EXPAND_FRAMES);
		if (ca.changedMethods > 0) {
			return cw.toByteArray();
		}
		return null;
	}

	/**
	 * Custom class adapter that modifies methods by an around advice.
	 * 
	 * @author Antonio S. R. Gomes
	 */
	private static class EnterExitClassAdapter extends ClassAdapter {
		Type classType;
		int changedMethods;
		Config config;

		public EnterExitClassAdapter(ClassVisitor cv, Config config) {
			super(cv);
			this.config = config;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			super.visit(version, access, name, signature, superName, interfaces);
			classType = Type.getType("L" + name + ";");
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			String[] names = makeMethodName(classType, name, desc);
			String globalName = names[0];
			String localName = names[1];
			if (canProfileMethod(classType, access, name, desc, signature, exceptions, config, globalName, localName)) {
				print(0,"Profiling method:"+globalName);
				if (changedMethods == 0) {
					Log.print(1, "Instrumenting class " + classType.getClassName());
				}
				int gmid = ThreadProfiler.newMethod(globalName);
				Log.print(3, "    method " + localName);
				MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
				EnterExitAdviceAdapter ma = new EnterExitAdviceAdapter(mv, access, name, desc, gmid,globalName);
				changedMethods++;
				return ma;
			}
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}

	private static class EnterExitAdviceAdapter extends AdviceAdapter {
		private int gmid;
		private String methodName;

		public EnterExitAdviceAdapter(MethodVisitor mv, int access, String name, String desc, int gmid,String globalName) {
			super(mv, access, name, desc);
			this.gmid = gmid;
			this.methodName = globalName;
		}

		@Override
		protected void onMethodEnter() {
			mv.visitLdcInsn(new String(methodName));
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "org/balazsbela/symbion/profiler/ThreadProfiler", "enterMethod", "(Ljava/lang/String;Ljava/lang/Object;)V");
		}

		@Override
		protected void onMethodExit(int opcode) {
			mv.visitLdcInsn(new String(methodName));
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "org/balazsbela/symbion/profiler/ThreadProfiler", "exitMethod", "(Ljava/lang/String;Ljava/lang/Object;)V");
		}
	}

	private static String[] makeMethodName(Type classType, String methodName, String methodDescriptor) {
		StringBuilder sbName = new StringBuilder();
		StringBuilder sbMethod = new StringBuilder();
		sbName.append(classType.getClassName());
		sbName.append(".");
		sbMethod.append(methodName);
		sbMethod.append("(");
		boolean comma = false;
		for (Type pt : Type.getArgumentTypes(methodDescriptor)) {
			if (comma) {
				sbMethod.append(",");
			}
			comma = true;
			sbMethod.append(pt.getClassName());
		}
		sbMethod.append(")");
		sbName.append(sbMethod);
		return new String[] { sbName.toString(), sbMethod.toString() };
	}

	private static boolean canProfileMethod(Type classType, int access, String name, String desc, String signature,
			String[] exceptions, Config config, String globalName, String localName) {
		if (((access & Opcodes.ACC_ABSTRACT) | (access & Opcodes.ACC_NATIVE) | (access & Opcodes.ACC_SYNTHETIC)) != 0) {
			return false;
		}

		if (globalName.startsWith("org.balazsbela.FirmManagement")) {
			//Log.print(0, "Checking if can profile:" + globalName);
		}

		List<Rule> rules = config.getRules();
		if (rules == null) {
			return false;
		}
		Rule selectedRule = null;
		for (Rule rule : rules) {
			if (globalName.startsWith("org.balazsbela.FirmManagement")) {
				//Log.print(0, "Checking pattern:" + rule.getPattern() + " for " + globalName);
			}

			if (rule.matches(globalName)) {
				//Log.print(0, "Matched rule!");
				selectedRule = rule;
				break;
			}
		}
		if (selectedRule == null) {
			return false;
		}
		if (selectedRule.getAction() == Rule.Action.ACCEPT) {
			return true;
		}

		return false;
	}

}
