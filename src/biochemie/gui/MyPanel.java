/*
 * Created on 11.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.gui;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyPanel extends JPanel {

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public MyPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param layout
     */
    public MyPanel(LayoutManager layout) {
        super(layout);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param isDoubleBuffered
     */
    public MyPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    public MyPanel() {
        super();
        // TODO Auto-generated constructor stub
    }
    /**
     * Setzt Hintergrundfarbe rekursiv bei allen enthaltenen JComponents.
     * @param string
     */
    public void setBackground(Color bg) {
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
}
