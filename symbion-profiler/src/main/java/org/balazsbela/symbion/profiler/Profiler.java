package org.balazsbela.symbion.profiler;

import static org.balazsbela.symbion.profiler.Log.print;

import java.lang.instrument.Instrumentation;

public class Profiler {

	static Transformer t;
	static Instrumentation inst;
	static Server server;
	static Config config;
	volatile static boolean beingShutdown;
	static final Object waitConnectionLock = new Object();

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

			Profiler.inst = inst;

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					beingShutdown = true;
					BytecodeTransformer.enabled = false;
					print(0, "Profiler stopped");
				}
			});
			if (config.isWaitConnection()) {
				print(0, "JVM waiting connection from Profiler4j Console...");
				synchronized (waitConnectionLock) {
					waitConnectionLock.wait();
				}
			}
		} catch (Throwable any) {
			print(0, "UNEXPECTED ERROR", any);
			System.exit(1);
		}
	}

}
