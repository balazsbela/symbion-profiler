package org.balazsbela.symbion.profiler;

import java.lang.reflect.Field;
import java.util.List;

public class ThreadProfiler {

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
	static final Object globalLock = new GlobalLock();

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
	 * Records a method's entry. This method is called by instrumented code.
	 * 
	 * @param globalMethodId
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @see #newMethod(String)
	 */
	public static void enterMethod(String globalMethod, Object o) {
		final Thread ct = Thread.currentThread();

		synchronized (globalLock) {
			
			String methodName = globalMethod.substring(0, globalMethod.indexOf("("));
			
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			int index = 0;
			String currentStMethod;
			for(int i=0;i<stackTraceElements.length;i++) {
				currentStMethod = stackTraceElements[i].getClassName()+"."+stackTraceElements[i].getMethodName();
//				System.out.println(currentStMethod);
				if(currentStMethod.contains(methodName)) {
					index=i;
					break;
				}
			}
			
						
			//One deeper in the stack trace
			StackTraceElement caller = stackTraceElements[index+1];		
			String globalMethodName = caller.getClassName() + " " + caller.getMethodName();
			
			if(methodMatchesRule(caller.getClassName(), caller.getMethodName())) {		
				System.out.println("--------------------------------------");						
				System.out.println("Entered:"+caller.getMethodName()+"->"+ globalMethod);									
				System.out.println("Current class:" + o.getClass().getName());
				System.out.println("Class has the following fields with the following values:");
				System.out.println("--------------------------------------");
				for (Field field : o.getClass().getDeclaredFields()) {
					try {
						field.setAccessible(true);
						System.out.println(field.getName() + " " + field.get(o));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ExecutionTimeline.calledMethods.add(globalMethod);
			}
			else {
				System.out.println("*********************************");						
				System.out.println("Entered:"+caller.getMethodName()+"->"+ globalMethod);									
				System.out.println("*********************************");
			}
		}
	}

	/**
	 * Records a method's exit. This method is called by instrumented code.
	 * 
	 * @param globalMethodId
	 * @see #newMethod(String)
	 */
	public static void exitMethod(String globalMethod, Object o) {
		final Thread ct = Thread.currentThread();
		synchronized (globalLock) {
//			System.out.println("--------------------------------------");
//			System.out.println("Exited:" + globalMethod + " from object of class:" + o.getClass().getName());
//			System.out.println("--------------------------------------");
		}
	}
	
	
	public static boolean methodMatchesRule(String className, String methodName) {
		String globalMethodName = className+"."+methodName;
		System.out.println("Checking if we can match:"+globalMethodName);
		List<Rule> rules = Agent.config.getRules();
		if (rules == null) {
			return false;
		}
		Rule selectedRule = null;
		for (Rule rule : rules) {
//			if (globalMethodName.startsWith("org.balazsbela.FirmManagement")) {
//				 Log.print(0, "Checking pattern:" + rule.getPattern() +
//				 " for " + globalMethodName);
//			}
			
			String pattern = rule.getPattern();
			String ruleClassName = pattern.substring(0,pattern.indexOf("(")-1);
			if (rule.matches(globalMethodName) || className.startsWith(ruleClassName)  ) {
//				 Log.print(0, "Matched rule!");
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
