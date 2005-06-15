/*
 * Created on 15.06.2004 by Steffen
 *
 */
package biochemie.sbe.calculators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import biochemie.sbe.multiplex.Multiplexer;



public  class ReusableThread extends Thread implements ActionListener{
    private final List tasklist;
    private Timer timer;
    private boolean resultAvailable,resultFetched;
    Interruptible i;
    private int msectowait;
    
    long lasttime;
    
    public ReusableThread( int msectowait) {
        System.out.println("Max. calculation time="+msectowait+"ms.");
        tasklist=new ArrayList();
        this.msectowait=msectowait;
        timer=new Timer(msectowait,this);
        timer.setRepeats(false);
        setDaemon(true);
        setUnavailable();
        start();
    }
    
    public void setInterruptableJob(Interruptible i) {
        synchronized(tasklist) {
	        tasklist.add(i);
	        tasklist.notify();
        }
    }
    public void run() {
        while (true) {
                synchronized(tasklist) {
                    while (tasklist.isEmpty()) {
                        try
                        {
                            tasklist.wait();
                        }
                        catch (InterruptedException ignored){}
                    }
                    if(tasklist.isEmpty())
                        continue;
                    i=(Interruptible) tasklist.remove(0);
                }
                setUnavailable();
                lasttime=System.currentTimeMillis();
                timer.start();
                i.start();
                timer.stop();	                
                wakeUp();
        }
    }

    public void actionPerformed(ActionEvent e) {
        long time=System.currentTimeMillis();
        System.out.println("timerevent occured after "+(lasttime - time));
        lasttime=time;
        stopTask();
    }
    /**
     * Stops the currently running task
     */
    public synchronized void stopTask() {
        if(0 != msectowait)
            i.stop();
    }

    private synchronized void setUnavailable() {
        resultFetched=false;
        resultAvailable=false;
    }
    private synchronized void wakeUp() {
        //System.out.println("notifying Mainthread...");
        resultAvailable=true;
        notifyAll();
        try {
            while(!resultFetched)
                wait();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
    
    /**
     * @return
     */
    public synchronized Object getResult() {
        try {
            while(!resultAvailable) {
                if(Multiplexer.isStopped())
                    return null;
                //System.out.println("Mainthread: wait()...");
                wait();
                //System.out.println("Mainthread: was notified!");
            }
        }catch(InterruptedException ie) {}
        resultFetched=true;
        resultAvailable=false;
        notifyAll();
        //System.out.println("Mainthread: fetching result...");
        return i.getResult();
    }
}