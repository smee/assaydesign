package biochemie.calcdalton;
import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import biochemie.calcdalton.gui.CDConfig;
import biochemie.calcdalton.gui.SBEGui;
import biochemie.calcdalton.gui.SBEPanel;

public class tf_seqDocListener implements DocumentListener{
  List sbep;
  CDConfig config;
  Border errorBorder;
  Border okayBorder;
  
    public tf_seqDocListener(List sbep)
    {
        this.sbep = sbep;
        config=CDConfig.getInstance();
    }

    public void insertUpdate(DocumentEvent e)
    {
        check();
    }

    public void check()
    {

        int len;
        int maxBr=config.getMaxBruchstelle();
        boolean okay=true;
        if(okayBorder == null)
            okayBorder=((SBEPanel)sbep.get(0)).tfSequence.getBorder();
        if(errorBorder == null)
            errorBorder=BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red,2),okayBorder);
        int sum=0;
        int num=sbep.size();
        for(int i=0;i<num;i++){
            SBEPanel sp=(SBEPanel)sbep.get(i);
        	len= sp.tfSequence.getText().length();
            sum+=len;
            if(len < maxBr && 0 != len){
                okay=false;
                sp.tfSequence.setBorder(errorBorder);
            }else{
                sp.tfSequence.setBorder(okayBorder);
            }
        }
        if(0 == sum)
            okay=false;
        SBEGui.getInstance().start.setEnabled(okay);

    }

    public void removeUpdate(DocumentEvent e)
    {
        check();
    }

    public void changedUpdate(DocumentEvent e)
    {
        check();
    }


  
}