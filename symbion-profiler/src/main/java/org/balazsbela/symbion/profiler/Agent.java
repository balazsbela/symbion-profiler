package org.balazsbela.symbion.profiler;

import static org.balazsbela.symbion.profiler.Log.print;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

import org.balazsbela.symbion.profiler.Rule;

public class Agent {

	static Transformer t;
	static Instrumentation inst;
	static Server server;
	static Config config;
	volatile static boolean beingShutdown;
	static final Object waitConnectionLock = new Object();
    static final Set<String> modifiedClassNames = new HashSet<String>();


	public static void premain(String args, Instrumentation inst) {

		try {
			config = new Config(args);
			server = new Server(config);
			server.start();
			try {
				// warm-up time (sometimes needed)
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}

			t = new Transformer(config);
			BytecodeTransformer.enabled = true;
			inst.addTransformer(t);

			Agent.inst = inst;

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					beingShutdown = true;
					BytecodeTransformer.enabled = false;
					print(0, "Profiler stopped");
				}
			});
			if (config.isWaitConnection()) {
				synchronized (waitConnectionLock) {
					print(0, "JVM waiting connection from console...");				
					waitConnectionLock.wait();
				}
			}
		} catch (Throwable any) {
			print(0, "UNEXPECTED ERROR", any);
			System.exit(1);
		}
	}

	
	
	 // *(*)
    // com.foo.*(*)
    // [a-zA-Z\\.\\-\\*]+\\([a-zA-Z\\.\\-\\*\\[\\]\\])
    private static boolean ruleMatchesClass(Rule r, Class c) {
        String s = r.getPattern();
        int p1 = s.indexOf('(');
        int p2 = s.indexOf(')');
        if (p1 > 0 && p2 > p1) {
            s = s.substring(0, p1);
            return Utils.getRegex(s).matcher(c.getName() + ".").matches();
        }
        throw new ProfilerError("Invalid rule pattern '" + s + "'");
    }
}
