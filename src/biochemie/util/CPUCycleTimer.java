/*
 * Created on 17.02.2004
 *
 */
package biochemie.util;


/**
 * Liest CPUCycles per JNI aus, die seit dem Start des Rechners vergangen sind.
 * @author Steffen
 *
 */
public class CPUCycleTimer {
    private static boolean enabled;
    static {
        try {
            System.loadLibrary("rdtsc");
            enabled=true;
        }catch (Exception e) {
            enabled=false;
        }
      }
     public native long rdtsc();
    private long time;
    private long cyclesPerMSecond=0;
    
    public CPUCycleTimer(){
         //need to test Speed for later Calculation of elapsed time 
         Thread t=new Thread(new Runnable(){
            public void run() {
                long t=rdtsc();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                cyclesPerMSecond=(rdtsc()-t)/1000;
            }             
         });
         t.run();   
     }
     public boolean isEnabled() {
        return enabled;
    }
    public void start(){
        time=rdtsc();
    }
    public double getTimeInMs(){
        return (double)(rdtsc()-time)/(double)cyclesPerMSecond;
    }
    public double getTimeInMs(long time){
        return (double)time/(double)cyclesPerMSecond;
    }
}
