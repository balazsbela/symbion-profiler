package org.balazsbela.symbion.profiler;

import static org.balazsbela.symbion.profiler.ThreadProfiler.globalLock;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import static org.balazsbela.symbion.profiler.Log.print;

public class Transformer implements ClassFileTransformer {
	private Config config;

	public Transformer(Config config) {
		this.config = config;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (Agent.beingShutdown) {
			return null;
		}
		className = className.replace('/', '.');
//		if(className.startsWith("org.balazsbela.")) {
//			print(0,"Inspecting class "+ className );
//			if(rejectByDefault(className)) {
//				print(0,"Rejected!");
//			}
//		}
		//print(0, "Inspecting classname:"+className);
		if (rejectByDefault(className)) {
			//print(0, "Rejecting");
			return null;
		}
		try {
			synchronized (globalLock) {
								
				return BytecodeTransformer.transform(className, loader, classfileBuffer, config);

			}
		} catch (Throwable t) {
			print(0, "Could not transform class " + className, t);
			if (config.isExitVmOnFailure()) {
				System.exit(1);
			}
		}
		return null;
	}

	
	/**
     * Checks whether a class should be simply ignored by the profiler in every possible
     * situation.
     * 
     * @param className Name of the class to check
     * @return <code>true</code> if the class should be rejected, <code>false</code>
     *         otherwise
     */
    public boolean rejectByDefault(String className) {
        if (className == null) {
            return true;
        }
        if (className.startsWith("org.balazsbela.symbion")) {
            return false;
        }
        //
        // *** WARNING ****
        // These entries were carefully chosen in order to avoid critical problems (JVM
        // crashes, stack overflows, cyclic dependencies, etc.). Only remove one of these
        // if you are really sure.
        // 
        // You´ve been warned, that is, _DON´T EMAIL ME ASKING FOR HELP_
        //
        if (className.startsWith("[")
                // Packages org.objectweb.asm.** are repackaged to be under
                // net.sf.profiler4j.agent., so we must comment this out
                // || className.startsWith("org.objectweb.asm.")
                || className.startsWith("org.objectweb.asm.")
                // These cause recursive calls through the profiler. Some of these can be
                // avoided (java.util) with some tweaks in the class ThreadProfiler. The
                // others are much more complex and would be viable only with a profiler
                // written entirely in native code. However, they don´t seem that
                // important anyway.
                || className.startsWith("java.util.")
                || className.startsWith("java.lang.")
                || className.startsWith("java.lang.Thread")
                || className.startsWith("java.lang.reflect.")
                || className.startsWith("java.lang.ref.")

                // These misteriously raise an java.lang.NoClassDefFoundError
                || className.equals("com.sun.tools.javac.util.DefaultFileManager")

                // These cause an ugly JVM crash! Pretty weird.
                || className.startsWith("sun.security.")
                || className.startsWith("java.security.MessageDigest$")
                || className.startsWith("sun.reflect.")

                // Dynamic proxies
                || className.startsWith("$Proxy") || className.contains("ByCGLIB$$")) {
            return true;
        }
       
        return false;

    }
}
