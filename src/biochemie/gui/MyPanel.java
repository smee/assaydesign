/*
 * Created on 11.01.2005
 *
 */
package biochemie.gui;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author sdienst
 *
 */
public abstract class MyPanel extends JPanel implements ChangeWatcher{
    private boolean dirtyFlag=false;
    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public MyPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * @param layout
     */
    public MyPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * @param isDoubleBuffered
     */
    public MyPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     *
     */
    public MyPanel() {
        super();
    }
    /**
     * Setzt Hintergrundfarbe rekursiv bei allen enthaltenen JComponents.
     * @param string
     */
    public void setBackground(Color bg) {
        super.setBackground(bg);
        int num=getComponentCount();
        for (int i = 0; i < num; i++) {
            getComponent(i).setBackground(bg);
        }
    }
    /**
     * Setzt Tooltip rekursiv bei allen enthaltenen JComponents.
     * @param string
     */
    public void setRekTooltip(String t) {
        setToolTipText(t);
        int num=getComponentCount();
        for (int i = 0; i < num; i++) {
            ((JComponent)getComponent(i)).setToolTipText(t);
        }
    }
    

    /* (non-Javadoc)
     * @see java.awt.Component#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        int num=getComponentCount();
        for (int i = 0; i < num; i++) {
            ((JComponent)getComponent(i)).setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }
    public boolean hasChanged() {
        return dirtyFlag;
    }

    public void setUnchanged() {
        dirtyFlag=false;
    }
    protected void dirty() {
        dirtyFlag=true;
    }
}
