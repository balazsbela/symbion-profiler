package org.balazsbela.symbion.profiler;


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
     * @see #newMethod(String)
     */
    public static void enterMethod(String globalMethod) {
        final Thread ct = Thread.currentThread();
   
        synchronized (globalLock) {
           System.out.println("Entered:"+ globalMethod);
           ExecutionTimeline.calledMethods.add(globalMethod);
        }
    }
    /**
     * Records a method's exit. This method is called by instrumented code.
     * 
     * @param globalMethodId
     * @see #newMethod(String)
     */
    public static void exitMethod(String globalMethod) {
        final Thread ct = Thread.currentThread();
        synchronized (globalLock) {
            System.out.println("Exited:"+globalMethod);

        }
    }

}
