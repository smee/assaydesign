/*
 * Created on 21.11.2004
 *
 */
package biochemie.calcdalton.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import biochemie.calcdalton.CDOptionsImpl;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.util.FileSelector;
import biochemie.util.config.GeneralConfig;


/**
 * @author Steffen Dienst
 *
 */
public class CDConfigPersistPanel extends JPanel {

	final private CDConfigPanel p;
    final private CDMassesConfigPanel m;

	public CDConfigPersistPanel(CDConfigPanel p, CDMassesConfigPanel massPanel){
		this.p=p;
        this.m=massPanel;
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
        btJb_save = new JButton( new SaveAction() );
        btJb_load = new JButton( new LoadAction() );
        btJb_default = new JButton( this.p.new ResetAction() );
        add(btJb_default,"1,1");
        add(btJb_load,"1,3,");
        add(btJb_save,"1,5");
	}

public class LoadAction extends AbstractAction {
    Icon icon;
    LoadAction() {
        putValue(NAME, "Load");
        putValue(SHORT_DESCRIPTION, "Load previously saved settings from disk.");
        java.net.URL url=this.getClass().getClassLoader().getResource("images/open.gif");
        if(null != url){
            icon=new ImageIcon(url);
            putValue(Action.SMALL_ICON,icon);
        }
    }
    public void actionPerformed(ActionEvent e) {
        File file=FileSelector.getUserSelectedFile(null,"Load config...",new FileFilter(){
            public boolean accept(File f) {
                if(f.isDirectory())
                    return true;
                if(f.isFile() && (f.getName().endsWith(".cfg") || f.getName().endsWith(".CFG")))
                    return true;
                return false;
            }
            public String getDescription() {
                return "CalcDalton-configfiles";
            }
        },FileSelector.OPEN_DIALOG);
        if(file!=null){
            CDOptionsImpl cfg = new CDOptionsImpl();
            try {
                cfg.readConfigFile(file.getCanonicalPath());
                p.setValuesFrom(cfg);
                m.setValuesFrom(cfg);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null,"Sorry  your personal settings couldn't be"
                                            +"loaded. An error occured.","", JOptionPane.WARNING_MESSAGE);
        }
        }
    }
}

public class SaveAction extends AbstractAction {
    Icon icon;
    SaveAction() {
        putValue(NAME, "Save");
        putValue(SHORT_DESCRIPTION, "Save settings to disk.");
        java.net.URL url=this.getClass().getClassLoader().getResource("images/save.gif");
        if(null != url){
            icon=new ImageIcon(url);
            putValue(Action.SMALL_ICON,icon);
        }
    }
    public void actionPerformed(ActionEvent e) {
        File file=FileSelector.getUserSelectedFile(null,"Save config...",new FileFilter(){
            public boolean accept(File f) {
                if(f.isDirectory())
                    return true;
                if(f.isFile() && (f.getName().endsWith(".cfg") || f.getName().endsWith(".CFG")))
                    return true;
                return false;
            }
            public String getDescription() {
                return "CalcDalton-configfiles";
            }
        },FileSelector.SAVE_DIALOG);
        if(file!=null){
            String path=file.getAbsolutePath();
            if(!path.endsWith(".cfg") && !path.endsWith(".CFG"))
                path += ".cfg";
            file=new File(path);
            try {
                file.createNewFile();
                CalcDaltonOptions cfg= p.getCalcDaltonOptionsProvider();
                p.saveToConfig(cfg);
                m.saveToConfig((CalcDaltonOptions)cfg);
                ((GeneralConfig) cfg).updateConfigFile(file.getCanonicalPath());
            } catch (FileNotFoundException e1) {
                JOptionPane.showMessageDialog(null,"Sorry – your personal settings couldn't be"
                                                +"saved. An error occured.","", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null,"Sorry – your personal settings couldn't be"
                                                +"saved. An error occured.","", JOptionPane.WARNING_MESSAGE);
            }
        }

    }
}
}
