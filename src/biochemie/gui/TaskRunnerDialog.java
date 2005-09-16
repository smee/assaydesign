/*
 * Created on 21.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import biochemie.sbe.multiplex.Multiplexer;
import biochemie.util.SwingWorker;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TaskRunnerDialog implements ActionListener {
    private SwingWorker task;
    private Timer timer;
    private ProgressMonitor monitor;
    private Component parent;
    private String title;
    
    public TaskRunnerDialog(String title, Component parent, SwingWorker task) {
        this.task=task;
        this.parent=parent;
        this.title=title;
        timer = new Timer(1000, this);
    }
    
    public void show() {
        if(parent != null)
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        monitor = new ProgressMonitor(parent,title,"Calculation running...",0,100);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);
        timer.start();
        task.start();
        monitor.setProgress(1);//TODO wirklichen Progress festhalten
    }
    /**
     * Gets called by the timer.
     */
    public void actionPerformed(ActionEvent e) {
        if(monitor.isCanceled() || task.isDone()) {
            timer.stop();
            task.interrupt();
            Multiplexer.stop(true);//XXX weg damit, soll nur per interrupt gehen, aber irgendwo haengts. DRINGEND!!!
            monitor.close();
            Toolkit.getDefaultToolkit().beep();
            if(parent != null)
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

}
