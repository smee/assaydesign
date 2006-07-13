package biochemie.calcdalton.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;

import biochemie.calcdalton.CalcDaltonOptions;

public class PeakInputPanel extends JPanel {
    private final static double p=TableLayoutConstants.PREFERRED;
    private final static double f=TableLayoutConstants.FILL;
    private final static double b=5;
    
    private JLabel label;
    private PBSequenceField lowPeakTf, highPeakTf, mediumMassTf;
    public PeakInputPanel() {
        super();
        initialize();
    }

    private void initialize() {
        double[][] peakSizes={{b,p,b,p,b,p,b,p,b,p,b,p,b,p,b},{b,p,b}};
        setLayout(new TableLayout(peakSizes));
        label=new JLabel("...between Foobar");
        lowPeakTf=new PBSequenceField(6,false,PBSequenceField.NUMBERS);
        lowPeakTf.setUniqueChars(".");
        highPeakTf=new PBSequenceField(6,false,PBSequenceField.NUMBERS);
        highPeakTf.setUniqueChars(".");
        mediumMassTf=new PBSequenceField(9,false,PBSequenceField.NUMBERS);
        mediumMassTf.setUniqueChars(".");
        
        add(label,"1,1");
        add(lowPeakTf,"3,1");
        add(new JLabel("under"),"5,1");
        add(highPeakTf,"7,1");
        add(new JLabel("D and"),"9,1");
        add(mediumMassTf,"11,1");
        add(new JLabel("above it"),"13,1");
    }
    public void setLabel(String l){
        label.setText(l);
    }
    public double getPeakSplitpoint(){
        return mediumMassTf.getAsDouble(-1);
    }
    public void setPeakValues(double[] vals){
        if(vals==null || vals.length <3)
            throw new IllegalArgumentException("call with 3 double values!");
        lowPeakTf.setText(Double.toString(vals[0]));
        mediumMassTf.setText(Double.toString(vals[1]));
        highPeakTf.setText(Double.toString(vals[2]));
        
    }
    public double[] getPeakValues(){
        return new double[]{lowPeakTf.getAsDouble(-1),mediumMassTf.getAsDouble(-1),highPeakTf.getAsDouble(-1)};
    }
}
