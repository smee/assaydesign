/*
 * Created on 21.11.2004
 *
 */
package biochemie.calcdalton.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JButton;
import javax.swing.JPanel;

import biochemie.calcdalton.gui.CDConfigPanel.LoadAction;
import biochemie.calcdalton.gui.CDConfigPanel.ResetAction;
import biochemie.calcdalton.gui.CDConfigPanel.SaveAction;




/**
 * @author Steffen Dienst
 *
 */
public class CDConfigPersistPanel extends JPanel {

	private CDConfigPanel p;

	public CDConfigPersistPanel(CDConfigPanel p){
		this.p=p;
		initialize();
	}

	private void initialize(){
        JButton btJb_save;
        JButton btJb_load;
        JButton btJb_default;
        double p=TableLayoutConstants.PREFERRED;
        double f=TableLayoutConstants.FILL;
        double b=5;
        double[][] fileSizes={{0.2,f,0.2},{3*b,p,3*b,p,b,p}};

        setLayout(new TableLayout(fileSizes));
        btJb_save = new JButton( this.p.new SaveAction() );
        btJb_load = new JButton( this.p.new LoadAction() );
        btJb_default = new JButton( this.p.new ResetAction() );
        add(btJb_default,"1,1");
        add(btJb_load,"1,3,");
        add(btJb_save,"1,5");
	}
}
