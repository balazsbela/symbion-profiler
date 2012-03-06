package org.balazsbela.symbion.profiler;

import java.lang.reflect.Field;


public class ThreadProfiler {
	
    /**
     * Current number of instrumented methods in current {@link #sessionId}. Whenever a
     * new session is created this value is set to 0.
     */
    private static int methodCount = 0;
    
	 /**
     * Global monitor used to implement mutual-exclusion. In the future this single
     * monitor may be broken up into many different monitors to reduce contention.
     */
    static final Object globalLock = new GlobalLock();

    private static class GlobalLock {
    }

    /**
     * Creates a new method object assigning a new GMId (Global Method Id)
     * <p>
     * A global method ID has two fundamental parts: bits 0-15 represent the method ID,
     * bits 16-23 represent the. This means that global IDs are unique
     * for any method and for any session in the same JVM execution.
     * 
     * @param methodName name of the method to trace
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
    public static void enterMethod(String globalMethod,Object o) {    	
        final Thread ct = Thread.currentThread();
   
        synchronized (globalLock) {
           System.out.println("Entered:"+ globalMethod);
           
           System.out.println("Current class:"+o.getClass().getName());
           System.out.println("Class has the following fields with the following values:");
           for(Field field : o.getClass().getDeclaredFields()) {
        	   try {
        		field.setAccessible(true);
				System.out.println(field.getName()+" "+field.get(o));
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
    }
    /**
     * Records a method's exit. This method is called by instrumented code.
     * 
     * @param globalMethodId
     * @see #newMethod(String)
     */
    public static void exitMethod(String globalMethod,Object o) {
        final Thread ct = Thread.currentThread();               
        synchronized (globalLock) {
            System.out.println("Exited:"+globalMethod + " from object of class:"+o.getClass().getName());

        }
    }

}
