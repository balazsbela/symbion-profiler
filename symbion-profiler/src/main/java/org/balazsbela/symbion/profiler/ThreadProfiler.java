package org.balazsbela.symbion.profiler;

import java.rmi.RemoteException;
import java.util.List;

import org.balazsbela.symbion.config.Rule;
import org.balazsbela.symbion.models.ExecutionTimeline;
import org.balazsbela.symbion.models.Function;
import org.balazsbela.symbion.models.FunctionCall;
import org.balazsbela.symbion.models.ThreadModel;
import org.springframework.remoting.rmi.RmiServiceExporter;

public class ThreadProfiler {

	public interface IPCServices {
		ExecutionTimeline retrieveTimeline();

		void stopProfiling();

		void startProfiling();
	}

	private static boolean debugEnabled = false;

	/**
	 * Current number of instrumented methods in current {@link #sessionId}.
	 * Whenever a new session is created this value is set to 0.
	 */
	private static int methodCount = 0;

	/**
	 * Global monitor used to implement mutual-exclusion. In the future this
	 * single monitor may be broken up into many different monitors to reduce
	 * contention.
	 */
	public static final Object globalLock = new GlobalLock();

	private static final ExecutionTimeline timeline = new ExecutionTimeline();

	private static class GlobalLock {
	}

	/**
	 * Creates a new method object assigning a new GMId (Global Method Id)
	 * <p>
	 * A global method ID has two fundamental parts: bits 0-15 represent the
	 * method ID, bits 16-23 represent the. This means that global IDs are
	 * unique for any method and for any session in the same JVM execution.
	 * 
	 * @param methodName
	 *            name of the method to trace
	 * @return global method id
	 */
	public static int newMethod(String methodName) {
		synchronized (globalLock) {
			int globalMethodId = (0 << 16) | methodCount;
			return globalMethodId;
		}
	}

	/**
	 * Record a method entry into the ExecutionTimeline
	 * 
	 * @param caller
	 *            - stacktrace element method calling
	 * @param callee
	 *            - stacktrace element method being called
	 */
	public static void recordMethodCall(StackTraceElement caller, StackTraceElement callee, String calleeMethodName,
			Thread ct) {

		synchronized (globalLock) {
			// System.out.println("TIMELINE:"+ timeline);

			Function parent = new Function();
			parent.setMethodName(caller.getMethodName());
			parent.setContainingClassName(caller.getClassName());
			parent.setLineNumber(caller.getLineNumber());

			Function target = new Function();
			target.setMethodName(callee.getMethodName());
			target.setContainingClassName(callee.getClassName());
			target.setLineNumber(callee.getLineNumber());
			String params = calleeMethodName.substring(calleeMethodName.indexOf("("));
			target.setMethodSignature(params);

			FunctionCall fc = new FunctionCall();
			fc.setTarget(target);
			fc.setParent(parent);
			fc.setLineNumber(callee.getLineNumber());

			ThreadModel tm = new ThreadModel();
			tm.setName(ct.getName());
			tm.setGroupName(ct.getThreadGroup().getName());
			tm.setPriority(ct.getPriority());
			tm.setState(ct.getState().toString());
			tm.setId(ct.getId());
			fc.setParentThread(tm);

			if (debugEnabled) {
				System.out.println("Recording method call:");
			}

			timeline.getCalledMethods().add(fc);

			// If a method was entered, it matched a rule.
			// Record it in the matched classes list.
			
			if(!timeline.getMatchedclasses().contains(target.getContainingClassName())) {
				timeline.getMatchedclasses().add(target.getContainingClassName());
			}
			if(!timeline.getThreads().contains(tm)) {
				timeline.getThreads().add(tm);
			}
			if (debugEnabled) {
				System.out.println("Registered:" + timeline.getCalledMethods().size());
				System.out.println(timeline);
			}
		}
	}

	/**
	 * Records a method's entry. This method is called by instrumented code.
	 * 
	 * @param globalMethodId
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @see #newMethod(String)
	 */
	public static void enterMethod(String globalMethod) {
		synchronized (globalLock) {

			if (debugEnabled) {
				System.out.println("Entered: " + globalMethod);
			}

			// if(!BytecodeTransformer.isEnabled()){
			// BytecodeTransformer.setSuccessfullyStopped(true);
			// if(debugEnabled) {
			// System.out.println("Bytecode transforming disabled! Exiting!");
			// }
			// return;
			// }

			final Thread ct = Thread.currentThread();
			String methodName = globalMethod.substring(0, globalMethod.indexOf("("));

			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			int index = 0;
			String currentStMethod;
			StackTraceElement callee = stackTraceElements[index];

			for (int i = 0; i < stackTraceElements.length; i++) {
				currentStMethod = stackTraceElements[i].getClassName() + "." + stackTraceElements[i].getMethodName();
				// if(debugEnabled) {
				// System.out.println(currentStMethod+" on line "+stackTraceElements[i].getLineNumber()+" in file "+stackTraceElements[i].getFileName());
				// }
				if (currentStMethod.contains(methodName)) {
					callee = stackTraceElements[i];
					index = i;
					break;
				}
			}

			StackTraceElement caller = null;
			if (stackTraceElements.length > index + 1) {
				// One deeper in the stack trace
				caller = stackTraceElements[index + 1];
			} else {
				caller = new StackTraceElement("Start Node", "Start Node", "", 0);
			}

			String globalMethodName = caller.getClassName() + " " + caller.getMethodName();

			if (debugEnabled) {
				if (org.balazsbela.symbion.profiler.Agent.getConfig() != null) {
					// System.out.println("Filter parents:"+Agent.config.isFilterParents());
				} else {
					System.out.println("Agent.config is null!");
				}
			}

			if ((Agent.getConfig() != null)
					&& ((!Agent.getConfig().isFilterParents()) || methodMatchesRule(caller.getClassName(),
							caller.getMethodName()))) {
				// if (debugEnabled) {
				// System.out.println("--------------------------------------");
				// System.out.println("Matched:" + caller.getClassName() + "." +
				// caller.getMethodName() + "->"
				// + globalMethod);
				// }

				// System.out.println("Current class:" +
				// o.getClass().getName());
				// System.out.println("Class has the following fields with the following values:");
				// System.out.println("--------------------------------------");
				// for (Field field : o.getClass().getDeclaredFields()) {
				// try {
				// field.setAccessible(true);
				// System.out.println(field.getName() + " " + field.get(o));
				// } catch (IllegalArgumentException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IllegalAccessException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

				recordMethodCall(caller, callee, globalMethod, ct);

			} else {
				if (debugEnabled) {
					System.out.println("*********************************");
					System.out.println("Entered:" + caller.getMethodName() + "->" + globalMethod);
					System.out.println("*********************************");
				}
			}
		}
	}

	/**
	 * Records a method's exit. This method is called by instrumented code.
	 * 
	 * @param globalMethodId
	 * @see #newMethod(String)
	 */
	public static void exitMethod(String globalMethod/** , Object o **/
	) {
		final Thread ct = Thread.currentThread();
		synchronized (globalLock) {
			if (debugEnabled) {
				System.out.println("--------------------------------------");
				System.out.println("Exited:" + globalMethod);// +
				// " from object of class:" + o.getClass().getName());
				System.out.println("--------------------------------------");
			}
		}
	}

	public static boolean methodMatchesRule(String className, String methodName) {
		if (className.startsWith("org.balazsbela.symbion")) {
			return false;
		}

		String globalMethodName = className + "." + methodName;
		if (debugEnabled) {
			System.out.println("Checking if we can match:" + globalMethodName);
		}
		List<Rule> rules = Agent.getConfig().getRules();
		if (rules == null) {
			return false;
		}
		Rule selectedRule = null;
		for (Rule rule : rules) {

			String pattern = rule.getPattern();
			String ruleClassName = pattern.substring(0, pattern.indexOf("(") - 1);
			if (rule.matches(globalMethodName) || className.startsWith(ruleClassName)) {

				if (debugEnabled) {
					Log.print(0, "Matched rule!");
				}
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

	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	public static void setDebugEnabled(boolean debugEnabled) {
		ThreadProfiler.debugEnabled = debugEnabled;
	}

	/**
	 * This may seem like over engineering, but I had a bug where the
	 * ThreadProfiler and the Server had a different copy of the timeline, even
	 * though both were static, this can happen when programs spawn new
	 * processes and static access is no longer available, the only way I could
	 * find to solve this problem was to use RMI as IPC.
	 */
	public static void startRMIService() {

		try {
			System.out.println("Starting RMI Service:");
			RmiServiceExporter serviceExporter = new RmiServiceExporter();
			serviceExporter.setServiceName("SymbionIPC");
			serviceExporter.setServiceInterface(ThreadProfiler.IPCServices.class);
			// serviceExporter.setRegistryHost(registryHost);
			serviceExporter.setRegistryPort(1099);
			serviceExporter.setService(new ThreadProfiler.IPCServices() {
				@Override
				public ExecutionTimeline retrieveTimeline() {
					System.out.println("RMI Call: retrieving timeline!");
					return ThreadProfiler.getTimeline();
				}

				@Override
				public void stopProfiling() {
					System.out.println("RMI Call : Stop profiling!");
					BytecodeTransformer.setEnabled(false);
				}

				@Override
				public void startProfiling() {
					System.out.println("RMI Call : Start profiling!");
					timeline.getCalledMethods().clear();
					timeline.getMatchedclasses().clear();
					timeline.getThreads().clear();
					BytecodeTransformer.setEnabled(true);

				}
			});
			serviceExporter.prepare();

			synchronized (Agent.rmiLock) {
				System.out.println("RMI Service started!");
				Agent.rmiLock.notifyAll();
			}
		} catch (RemoteException e) {
			// TODO: handle exception
		}

	}

	protected static ExecutionTimeline getTimeline() {
		return timeline;
	}

}
