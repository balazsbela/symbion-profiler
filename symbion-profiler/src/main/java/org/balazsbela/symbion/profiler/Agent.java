package org.balazsbela.symbion.profiler;

import static org.balazsbela.symbion.profiler.Log.print;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

import org.balazsbela.symbion.config.Config;
import org.balazsbela.symbion.config.Rule;
import org.balazsbela.symbion.errors.ProfilerError;
import org.balazsbela.symbion.models.ExecutionTimeline;
import org.balazsbela.symbion.utils.Utils;

public class Agent {

	private static Transformer t;
	private static Instrumentation inst;
	private static Server server;
	private static final Config config = new Config();
	volatile static boolean beingShutdown;
	static final Object waitConnectionLock = new Object();
	static final Object rmiLock = new Object();

    static final Set<String> modifiedClassNames = new HashSet<String>();
	public static void premain(String args, Instrumentation inst) {

		try {
					
			server = new Server(config);
			server.start();		
			try {
				// warm-up time (sometimes needed)
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}

			if (config.isWaitConnection()) {
				synchronized (waitConnectionLock) {
					print(0, "JVM waiting connection from console...");				
					waitConnectionLock.wait();
				}
			}
					
			t = new Transformer(config);
			BytecodeTransformer.setEnabled(true);
			inst.addTransformer(t);

			Agent.inst = inst;

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					beingShutdown = true;
					BytecodeTransformer.setEnabled(false);
					print(0, "Profiler stopped");
				}
			});
			
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


	public static Config getConfig() {
		return config;
	}

	public static Server getServer() {
		return server;
	}

}