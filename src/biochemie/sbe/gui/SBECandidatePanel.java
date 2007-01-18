/*
 * Created on 11.11.2004
 *
 */
package biochemie.sbe.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.ArrayUtils;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.domspec.CleavablePrimer;
import biochemie.gui.MyPanel;
import biochemie.gui.NuklSelectorPanel;
import biochemie.gui.PLSelectorPanel;
import biochemie.gui.StringEntryPanel;
import biochemie.sbe.CleavablePrimerFactory;
import biochemie.sbe.MiniSBE;
import biochemie.sbe.PinpointPrimerFactory;
import biochemie.sbe.PrimerFactory;
import biochemie.sbe.ProbePrimerFactory;
import biochemie.sbe.ProbePrimerFactoryTest;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.io.SBEPrimerReader;
import biochemie.util.Helper;
/**
 * @author Steffen Dienst
 *
 */
public class SBECandidatePanel extends MyPanel {
    private final int assayType;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private SBESequenceTextField seq5tf = null;
    private SBESequenceTextField seq3tf = null;
    private NuklSelectorPanel nuklSelectorPanel = null;
    private JTextField tfId = null;
    private PLSelectorPanel plpanel5 = null;
    private JLabel jLabel2 = null;
    private JTextField tfplexid = null;
    private PBSequenceField pcrlenTf = null;
    private HairpinSelectionPanel hairpin5SelectionPanel = null;
    private HairpinSelectionPanel hairpin3SelectionPanel = null;
    private StringEntryPanel multiplexidPanel = null;
    private StringEntryPanel pcrLenPanel = null;
    
    private boolean isExpertMode;
    private StringEntryPanel filtersPanel = null;
    private JCheckBox fixedPrimerCB = null;
    public class MyChangeListener implements DocumentListener, ChangeListener{
        public void changedUpdate(DocumentEvent e) {
            dirty();
        }
        public void insertUpdate(DocumentEvent e) {
            dirty();
        }
        public void removeUpdate(DocumentEvent e) {
            dirty();            
        }
        public void stateChanged(ChangeEvent e) {
            dirty();
        }    
    };
    private MyChangeListener cl=new MyChangeListener();
    private ISeqInputController inputcontrollerR;
    private ISeqInputController inputcontrollerL = null;
    
    private PLSelectorPanel plpanel3 = null;
    private PLSelectorPanel pinpoint5;
    private PLSelectorPanel pinpoint3;
    private ProbeTypeSelectorPanel probePanel5;
    private ProbeTypeSelectorPanel probePanel3;
    /**
     * This is the default constructor
     * @param
     */
    public SBECandidatePanel(String id, int maxlen, int num, int assayType) {
        super();
        this.assayType=assayType;
        initialize(maxlen, num);
        getTfId().setText(id);
    }
    
    /**
     * This method initializes this
     * @param num 
     *
     * @return void
     */
    private  void initialize(int minlen, int num) {
        GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
        jLabel2 = new JLabel();
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        jLabel1 = new JLabel();
        jLabel = new JLabel();
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        this.setLayout(new GridBagLayout());
        //this.setSize(864, 198);
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SBE-Primer "+num, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        jLabel.setText("ID");
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets(10,10,10,5);
        gridBagConstraints31.gridx = 0;
        gridBagConstraints31.gridy = 1;
        gridBagConstraints31.weightx = 0.0D;
        gridBagConstraints31.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints31.insets = new java.awt.Insets(0,10,5,10);
        gridBagConstraints31.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints7.gridx = 5;
        gridBagConstraints7.gridy = 0;
        gridBagConstraints7.gridheight = 2;
        gridBagConstraints7.weightx=1;
        gridBagConstraints7.insets = new java.awt.Insets(5,10,5,0);
        gridBagConstraints4.gridx = 2;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.insets = new java.awt.Insets(10,0,10,0);
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints4.gridwidth = 2;
        jLabel1.setText("5'-Sequence");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints5.gridx = 3;
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridwidth = 2;
        gridBagConstraints5.insets = new java.awt.Insets(0,0,5,0);
        gridBagConstraints11.gridx = 7;
        gridBagConstraints11.gridy = 0;
        gridBagConstraints11.gridheight = 2;
        gridBagConstraints11.insets = new java.awt.Insets(5,10,5,0);
        gridBagConstraints10.gridx = 9;
        gridBagConstraints10.gridy = 0;
        gridBagConstraints10.insets = new java.awt.Insets(10,10,10,0);
        gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
        jLabel2.setText("3'-Sequence");
        gridBagConstraints12.gridx = 9;
        gridBagConstraints12.gridy = 1;
        gridBagConstraints12.weightx = 2.0;
        gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints12.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints14.gridx = 10;
        gridBagConstraints14.gridy = 0;
        gridBagConstraints14.insets = new java.awt.Insets(10,10,10,0);
        
        gridBagConstraints2.gridx = 11;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.insets = new java.awt.Insets(10,10,10,0);
        
        gridBagConstraints41.gridx = 6;
        gridBagConstraints41.gridy = 0;
        gridBagConstraints41.gridheight = 2;
        gridBagConstraints41.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints42.gridx = 11;
        gridBagConstraints42.gridy = 0;
        gridBagConstraints42.gridheight = 2;
        gridBagConstraints42.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints51.gridx = 12;
        gridBagConstraints51.gridy = 0;
        gridBagConstraints51.gridheight = 2;
        gridBagConstraints51.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints6.gridx = 13;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.gridheight = 2;
        gridBagConstraints6.insets = new java.awt.Insets(0,10,0,0);
        this.add(getTfId(), gridBagConstraints31);
        this.add(jLabel, gridBagConstraints1);
        if(assayType==MiniSBE.CLEAVABLE || assayType==MiniSBE.PROBE_CLEAVABLE){
            inputcontrollerL = new SBESeqInputController(this,minlen,true);
            inputcontrollerR = new SBESeqInputController(this,minlen,false);
        }else{
            inputcontrollerL = new GeneralSeqInputController(this, minlen, true);
            inputcontrollerR = new GeneralSeqInputController(this, minlen, false);
        }
        inputcontrollerL.setOtherController(inputcontrollerR);
        inputcontrollerR.setOtherController(inputcontrollerL);
        
        setExpertMode(false);
        gridBagConstraints13.gridx = 14;
        gridBagConstraints13.gridy = 0;
        gridBagConstraints13.gridheight = 2;
        gridBagConstraints13.insets = new java.awt.Insets(10,10,10,10);
        gridBagConstraints15.gridx = 1;
        gridBagConstraints15.gridy = 1;
        gridBagConstraints15.insets = new java.awt.Insets(0,0,0,5);
        gridBagConstraints16.gridx = 10;
        gridBagConstraints16.gridy = 0;
        gridBagConstraints16.weightx=1;
        gridBagConstraints16.gridheight = 2;
        gridBagConstraints16.insets = new java.awt.Insets(5,10,5,0);
        this.add(getSeq5AssayDataComponent(), gridBagConstraints7);
        this.add(jLabel2, gridBagConstraints10);
        this.add(getSeq3tf(), gridBagConstraints12);
        this.add(getSNPSelectorPanel(), gridBagConstraints11);
        this.add(getHairpin5SelectionPanel(), gridBagConstraints41);
        this.add(getHairpin3SelectionPanel(), gridBagConstraints42);
        this.add(getMultiplexidPanel(), gridBagConstraints51);
        this.add(getPcrLenPanel(), gridBagConstraints6);
        this.add(getFiltersPanel(), gridBagConstraints13);
        this.add(getSeq3AssayDataComponent(), gridBagConstraints16);
        this.add(jLabel1, gridBagConstraints4);
        this.add(getSeq5tf(), gridBagConstraints5);
        this.add(getFixedPrimerCB(), gridBagConstraints15);
        setUnchanged();
    }
    protected Component getSeq5AssayDataComponent() {
        switch (assayType) {
        case MiniSBE.CLEAVABLE:
            return getPlpanel5();
        case MiniSBE.PINPOINT:
            return getPinpointAddonPanel5();
        case MiniSBE.PROBE:
            return getProbePanel5();
        case MiniSBE.PROBE_CLEAVABLE:
            MyPanel p=new MyPanel();
            p.add(getPlpanel5());
            p.add(getProbePanel5());
            return p;
        case MiniSBE.PROBE_PINPOINT:
            MyPanel pp=new MyPanel();
            pp.add(getPinpointAddonPanel5());
            pp.add(getProbePanel5());
            return pp;
        default:
            throw new IllegalArgumentException("Unknown assaytype "+assayType+", don't know how to create gui.");
        }
    }
    
    protected Component getSeq3AssayDataComponent() {
        switch (assayType) {
        case MiniSBE.CLEAVABLE:
            return getPlpanel3();
        case MiniSBE.PINPOINT:
            return getPinpointAddonPanel3();
        case MiniSBE.PROBE:
            return getProbePanel3();
        case MiniSBE.PROBE_CLEAVABLE:
            MyPanel p=new MyPanel();
            p.add(getPlpanel3());
            p.add(getProbePanel3());
            return p;
        case MiniSBE.PROBE_PINPOINT:
            MyPanel pp=new MyPanel();
            pp.add(getPinpointAddonPanel3());
            pp.add(getProbePanel3());
            return pp;
        default:
            throw new IllegalArgumentException("Unknown assaytype "+assayType+", don't know how to create gui.");
        }
    }
    
    /**
     * This method initializes PBSequenceField
     *
     * @return biochemie.calcdalton.gui.PBSequenceField
     */
    protected SBESequenceTextField getSeq5tf() {
        if (seq5tf == null) {
            seq5tf = new SBESequenceTextField();
            seq5tf.setMaxLen(100);
            seq5tf.setUpper(true);
            seq5tf.setColumns(15);
            seq5tf.cutFront(true);
            seq5tf.getDocument().addDocumentListener(cl);
        }
        return seq5tf;
    }
    /**
     * This method initializes nuklSelectorPanel
     *
     * @return biochemie.gui.NuklSelectorPanel
     */
    public NuklSelectorPanel getSNPSelectorPanel() {
        if (nuklSelectorPanel == null) {
            nuklSelectorPanel = new NuklSelectorPanel();
            nuklSelectorPanel.setRekTooltip("Define SNP alleles");
        }
        return nuklSelectorPanel;
    }
    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTfId() {
        if (tfId == null) {
            tfId = new JTextField();
            tfId.setPreferredSize(new java.awt.Dimension(80,20));
            tfId.setColumns(4);
            tfId.getDocument().addDocumentListener(new DocumentListener() {
                
                public void insertUpdate(DocumentEvent e) {
                    updateTooltip();
                }
                
                public void removeUpdate(DocumentEvent e) {
                    updateTooltip();
                }
                
                public void changedUpdate(DocumentEvent e) {
                    updateTooltip();
                }
                private void updateTooltip() {
                    dirty();
                    if(tfId.getText().length() == 0)
                        tfId.setToolTipText("Input SB-Primer Identification");
                    else
                        tfId.setToolTipText(tfId.getText());
                }
                
            });
            tfId.setToolTipText("Input SB-Primer Identification");
        }
        return tfId;
    }
    public String getId() {
        return getTfId().getText().trim();
    }
    /**
     * This method initializes plpanel5
     *
     * @return biochemie.gui.plpanel5
     */
    protected PLSelectorPanel getPlpanel5() {
        if (plpanel5 == null) {
            plpanel5 = new PLSelectorPanel();
            plpanel5.setTitle("Linker 5'");
            plpanel5.setPreferredSize(new java.awt.Dimension(90,56));
        }
        return plpanel5;
    }
    protected PLSelectorPanel getPinpointAddonPanel5() {
        if (pinpoint5 == null) {
//            pinpoint5 = new StringEntryPanel("Length of dT 3'");
//            pinpoint5.setValidChars("-0123456789");
//            pinpoint5.setUniqueChars("-");
//            pinpoint5.setColumns(5);
            pinpoint5=new PLSelectorPanel();
            pinpoint5.setTitle("Length of dT 3\'");
            int[] vals=new int[100];
            for (int i = 0; i < vals.length; i++) {
                vals[i]=i;
            }
            pinpoint5.setValues(vals);
            pinpoint5.setPreferredSize(new java.awt.Dimension(90,56));
        }
        return pinpoint5;
    }
    protected PLSelectorPanel getPinpointAddonPanel3() {
        if (pinpoint3 == null) {
//            pinpoint3 = new StringEntryPanel("Length of dT 3'");
//            pinpoint3.setValidChars("-0123456789");
//            pinpoint3.setUniqueChars("-");
//            pinpoint3.setColumns(5);
//            pinpoint3.setPreferredSize(new java.awt.Dimension(90,56));
            pinpoint3=new PLSelectorPanel();
            pinpoint3.setTitle("Length of dT 5\'");
            int[] vals=new int[100];
            for (int i = 0; i < vals.length; i++) {
                vals[i]=i;
            }
            pinpoint3.setValues(vals);
            pinpoint3.setPreferredSize(new java.awt.Dimension(90,56));
        }
        return pinpoint3;
    }
    private ProbeTypeSelectorPanel getProbePanel5() {
        if(probePanel5==null){
            probePanel5=new ProbeTypeSelectorPanel();
            probePanel5.setTitle("Probe type 5'");
            probePanel5.setPreferredSize(new java.awt.Dimension(140,56));
        }
        return probePanel5;
    }
    private ProbeTypeSelectorPanel getProbePanel3() {
        if(probePanel3==null){
            probePanel3=new ProbeTypeSelectorPanel();
            probePanel3.setTitle("Probe type 3'");
            probePanel3.setPreferredSize(new java.awt.Dimension(140,56));
        }
        return probePanel3;
    }
    /**
     * This method initializes PBSequenceField
     *
     * @return biochemie.calcdalton.gui.PBSequenceField
     */
    protected SBESequenceTextField getSeq3tf() {
        if (seq3tf == null) {
            seq3tf = new SBESequenceTextField(100,true,"ACGTacgt");
            seq3tf.setColumns(15);
            seq3tf.cutFront(false);
            seq3tf.getDocument().addDocumentListener(cl);
        }
        return seq3tf;
    }
    /**
     * This method initializes hairpinSelectionPanel
     *
     * @return biochemie.sbe.gui.HairpinSelectionPanel
     */
    protected HairpinSelectionPanel getHairpin5SelectionPanel() {
        if (hairpin5SelectionPanel == null) {
            hairpin5SelectionPanel = new HairpinSelectionPanel(assayType!=MiniSBE.PROBE && assayType!=MiniSBE.PROBE_CLEAVABLE && assayType!=MiniSBE.PROBE_PINPOINT);
            hairpin5SelectionPanel.setTitle("Def. Hairpin 5'");
        }
        return hairpin5SelectionPanel;
    }
    public void setExpertMode(boolean expert){
        this.isExpertMode=expert;
        getHairpin5SelectionPanel().setVisible(expert);
        getHairpin3SelectionPanel().setVisible(expert);
        getPcrLenPanel().setVisible(expert);
        getMultiplexidPanel().setVisible(expert);
        getFiltersPanel().setVisible(expert);
        
    }
    /**
     * This method initializes hairpinSelectionPanel1
     *
     * @return biochemie.sbe.gui.HairpinSelectionPanel
     */
    protected HairpinSelectionPanel getHairpin3SelectionPanel() {
        if (hairpin3SelectionPanel == null) {
            hairpin3SelectionPanel = new HairpinSelectionPanel(assayType!=MiniSBE.PROBE && assayType!=MiniSBE.PROBE_CLEAVABLE && assayType!=MiniSBE.PROBE_PINPOINT);
            hairpin3SelectionPanel.setTitle("Def. Hairpin 3'");
        }
        return hairpin3SelectionPanel;
    }
    /**
     * This method initializes stringEntryPanel
     *
     * @return biochemie.gui.StringEntryPanel
     */
    protected StringEntryPanel getMultiplexidPanel() {
        if (multiplexidPanel == null) {
            multiplexidPanel = new StringEntryPanel();
            multiplexidPanel.setLabel("MultiplexID");
            multiplexidPanel.setColumns(8);
            multiplexidPanel.setResizeToStringLen(true);
            multiplexidPanel.setRekTooltip("<html>Please insert the Multiplex ID of Primers <br>" +
                    "belonging to the same Multiplex by default. <br>" +
            "(auto=best Multiplex IDs are selected by the program)</html>");
        }
        return multiplexidPanel;
    }
    public String getCSVInputHeader(){
        return SBEPrimerReader.getCSVInputHeader(assayType);
    }
    /**
     * This method initializes stringEntryPanel1
     *
     * @return biochemie.gui.StringEntryPanel
     */
    protected StringEntryPanel getPcrLenPanel() {
        if (pcrLenPanel == null) {
            pcrLenPanel = new StringEntryPanel();
            pcrLenPanel.setLabel("PCR-product length");
            pcrLenPanel.setColumns(4);
            pcrLenPanel.setRekTooltip("Enter the length of the pcr product.");
            pcrLenPanel.setText("0");
        }
        return pcrLenPanel;
    }
    public String getCSVInputLine() {
        String id=getTfId().getText();
        String seq5=getSeq5tf().getText();
        if(assayType==MiniSBE.CLEAVABLE || assayType==MiniSBE.PROBE_CLEAVABLE)
            seq5=((SBESeqInputController) inputcontrollerL).getSequenceWOL();
        String seq3=getSeq3tf().getText();
        if(assayType==MiniSBE.CLEAVABLE || assayType==MiniSBE.PROBE_CLEAVABLE)
            seq3=((SBESeqInputController) inputcontrollerR).getSequenceWOL();
        String bautein5=getHairpin5SelectionPanel().getSelectedNukleotides();
        String bautein3=getHairpin3SelectionPanel().getSelectedNukleotides();
        String multiplexid = getMultiplexidPanel().getText();
        String filters = getFiltersPanel().getText();
        String productlen=getPcrLenPanel().getText();
        String snp=getSNPSelectorPanel().getSelectedNukleotides();
        boolean isFixed=getFixedPrimerCB().isSelected();
        StringBuffer sb=new StringBuffer();
        sb.append(id);
        sb.append(';');
        sb.append(seq5);
        sb.append(';');
        switch (assayType) {
        case MiniSBE.CLEAVABLE:
            sb.append(getPlpanel5().getSelectedValue());
            break;
        case MiniSBE.PINPOINT:
            sb.append(getPinpointAddonPanel5().getSelectedValue());
            break;
        case MiniSBE.PROBE:
            sb.append(getProbePanel5().getSelectedType());
            break;
        case MiniSBE.PROBE_CLEAVABLE:
            sb.append(getProbePanel5().getSelectedType());
            sb.append(";");
            sb.append(getPlpanel5().getSelectedValue());
            break;
        case MiniSBE.PROBE_PINPOINT:
            sb.append(getProbePanel5().getSelectedType());
            sb.append(";");
            sb.append(getPinpointAddonPanel5().getSelectedValue());
            break;
        default:
            throw new IllegalArgumentException("unknown assaytype '"+assayType+"', don't know how to save it!");
        }
        sb.append(';');
        sb.append(bautein5);
        sb.append(';');
        sb.append(snp);
        sb.append(';');
        sb.append(seq3);
        sb.append(';');
        switch (assayType) {
        case MiniSBE.CLEAVABLE:
            sb.append(getPlpanel3().getSelectedValue());
            break;
        case MiniSBE.PINPOINT:
            sb.append(getPinpointAddonPanel3().getSelectedValue());
            break;
        case MiniSBE.PROBE:
            sb.append(getProbePanel3().getSelectedType());
            break;
        case MiniSBE.PROBE_CLEAVABLE:
            sb.append(getProbePanel3().getSelectedType());
            sb.append(";");
            sb.append(getPlpanel3().getSelectedValue());
            break;
        case MiniSBE.PROBE_PINPOINT:
            sb.append(getProbePanel3().getSelectedType());
            sb.append(";");
            sb.append(getPinpointAddonPanel3().getSelectedValue());
            break;
        default:
            throw new IllegalArgumentException("unknown assaytype '"+assayType+"', don't know how to save it!");
        }
        sb.append(';');
        sb.append(bautein3);
        sb.append(';');
        sb.append(productlen);
        sb.append(';');
        sb.append(multiplexid);
        sb.append(';');
        sb.append(filters);
        sb.append(';');
        sb.append(isFixed);
        
        return sb.toString();
        
    }

    private CleavablePrimerFactory getCleavablePrimerFactory(SBEOptions cfg, boolean rememberoutput, String id, String seq5, String seq3, String snp, String bautein5, String bautein3, int pcrlen, String multiplexid, String unwanted, boolean userGiven){
        if(!inputcontrollerL.isOkay()) {
            return null;
        }
        seq5=((SBESeqInputController) this.inputcontrollerL).getSequenceWOL();
        int pl5=getPlpanel5().getSelectedValue();
        seq3=((SBESeqInputController) this.inputcontrollerR).getSequenceWOL();
        int pl3=getPlpanel3().getSelectedValue();
        if(seq5.length() == 0 && seq3.length()==0) //keine primer da
            return null;
        
        CleavablePrimerFactory s=new CleavablePrimerFactory(cfg,id,seq5,pl5,seq3,pl3,snp,pcrlen,bautein5,bautein3,multiplexid,unwanted,userGiven,rememberoutput);
        return s;
    }
    private PrimerFactory getPinpointPrimerFactory(SBEOptions cfg, boolean b, String id, String seq5, String seq3, String snp, String bautein5, String bautein3, int pcrlen, String multiplexid, String unwanted, boolean userGiven) {
        int tCount5=getPinpointAddonPanel5().getSelectedValue();
        int tCount3=getPinpointAddonPanel3().getSelectedValue();
        return new PinpointPrimerFactory(cfg,id,seq5,snp,seq3,bautein5,bautein3,tCount5,tCount3,pcrlen,multiplexid,unwanted,userGiven,b);
    }
    private PrimerFactory getProbePrimerFactory(SBEOptions cfg, boolean b, String id, String seq5, String seq3, String snp, String bautein5, String bautein3, int pcrlen, String multiplexid, String unwanted, boolean userGiven) {
        int probeType5=getProbePanel5().getSelectedType();
        int probeType3=getProbePanel3().getSelectedType();
        return new ProbePrimerFactory(cfg,id,seq5,snp,seq3,bautein5,bautein3,pcrlen,multiplexid,probeType5,probeType3,userGiven,unwanted,b);
    }
    /**
     * TODO listenerartig basteln...
     * @param optionsFromGui
     */
    public void refreshData(SBEOptions cfg) {
        switch (assayType) {
            case MiniSBE.CLEAVABLE:
                getPlpanel5().setValues(cfg.getPhotolinkerPositions());
                getPlpanel3().setValues(cfg.getPhotolinkerPositions());
            break;
            case MiniSBE.PINPOINT:
                break;
            case MiniSBE.PROBE:
                break;
            case MiniSBE.PROBE_CLEAVABLE:
                break;
            case MiniSBE.PROBE_PINPOINT:
                break;
        default:
            break;
        }
    }
    
    /**
     * TODO auch Ausgabedateien lesen!!!
     * @param id
     */
    public void setValuesFromCSVInputLine(String line) {
        dirty();
        StringTokenizer stok = new StringTokenizer(line,";\"");
        String id = stok.nextToken().trim();
        String l = stok.nextToken().trim();
        int assay5=-1,assay5_1=-1;
        try{
            assay5=Integer.parseInt(stok.nextToken().trim());
        }catch(NumberFormatException e){}
        if(assayType==MiniSBE.PROBE_CLEAVABLE  || assayType==MiniSBE.PROBE_PINPOINT){
            try{
                assay5_1=Integer.parseInt(stok.nextToken().trim());
            }catch(NumberFormatException e){}
        }
        String bautein5 = stok.nextToken();
        String snp = stok.nextToken();
        String r = stok.nextToken();
        int assay3=-1,assay3_1=-1;
        try{
            assay3=Integer.parseInt(stok.nextToken().trim());
        }catch(NumberFormatException e){}
        if(assayType==MiniSBE.PROBE_CLEAVABLE  || assayType==MiniSBE.PROBE_PINPOINT){
            try{
                assay3_1=Integer.parseInt(stok.nextToken().trim());
            }catch(NumberFormatException e){}
        }
        String bautein3 = stok.nextToken();
        int productlen=0;
        String temp=stok.nextToken();
        try{
            productlen=Integer.parseInt(temp);
        }catch (NumberFormatException e) {
            productlen=temp.length() ;//PCR-Produktlaenge
        }
        String multiplexid = stok.hasMoreTokens()?stok.nextToken():"";
        String filters = stok.hasMoreTokens()?stok.nextToken():"";
        String fixed = stok.hasMoreTokens()?stok.nextToken():"";
        
        getTfId().setText(id);
        getSeq5tf().setText(l);
        getHairpin5SelectionPanel().setSelectedNukleotides(bautein5);
        getSNPSelectorPanel().setSelectedNukleotides(snp);
        getSeq3tf().setText(r);
        getHairpin3SelectionPanel().setSelectedNukleotides(bautein3);
        getPcrLenPanel().setText(Integer.toString(productlen));
        switch (assayType) {
        case MiniSBE.CLEAVABLE:
            getPlpanel5().setSelectedValue(assay5);
            getPlpanel3().setSelectedValue(assay3);
            break;
        case MiniSBE.PINPOINT:
            getPinpointAddonPanel5().setSelectedValue(assay5);
            getPinpointAddonPanel3().setSelectedValue(assay3);
            break;
        case MiniSBE.PROBE:
            getProbePanel5().setSelectedType(assay5);
            getProbePanel3().setSelectedType(assay3);
            break;
        case MiniSBE.PROBE_CLEAVABLE:
            getProbePanel5().setSelectedType(assay5);
            getProbePanel3().setSelectedType(assay3);
            getPlpanel5().setSelectedValue(assay5_1);
            getPlpanel3().setSelectedValue(assay3_1);
            break;
        case MiniSBE.PROBE_PINPOINT:
            getProbePanel5().setSelectedType(assay5);
            getProbePanel3().setSelectedType(assay3);
            getPinpointAddonPanel5().setSelectedValue(assay5_1);
            getPinpointAddonPanel3().setSelectedValue(assay3_1);
            break;
        default:
            break;
        }
        getMultiplexidPanel().setText(multiplexid);
        getFiltersPanel().setText(filters);
        getFixedPrimerCB().setSelected(Boolean.valueOf(fixed).booleanValue());
    }
    public void setValuesFromCSVOutputLine(String line) {
        dirty();
        StringTokenizer st=new StringTokenizer(line,";\"");
        st.nextToken();//mid
        String id=st.nextToken();
        st.nextToken();//seq. bio....
        String snp=st.nextToken();
        int assay1=-1,assay2=-1;
        try {
            String val=st.nextToken();
            if(assayType==MiniSBE.PROBE || assayType==MiniSBE.PROBE_CLEAVABLE ||assayType==MiniSBE.PROBE_PINPOINT)
                assay1=ArrayUtils.indexOf(ProbePrimerFactory.ASSAYTYPES_DESC,val);
            else
                assay1=Integer.parseInt(val);
        }catch (NumberFormatException e) {
        }
        if(assayType==MiniSBE.PROBE_CLEAVABLE ||assayType==MiniSBE.PROBE_PINPOINT)
            try {
                assay2=Integer.parseInt(st.nextToken());
                
            }catch (NumberFormatException e) {
            }
            
            for(int i=0;i<5;i++)
                st.nextToken();
            boolean is5Seq=st.nextToken().trim().equals(CleavablePrimer._5_);
            String prodlen=st.nextToken();
            String seq=st.nextToken();        
            String otherseq=line.substring(line.lastIndexOf(';'));
            getTfId().setText(id);
            if(is5Seq) {
                getSeq5tf().setText(seq);
                switch (assayType) {
                case MiniSBE.CLEAVABLE:
                    getPlpanel5().setSelectedValue(assay1);
                    break;
                case MiniSBE.PINPOINT:
                    getPinpointAddonPanel5().setSelectedValue(assay1);
                    break;
                case MiniSBE.PROBE:
                    getProbePanel5().setSelectedType(assay1);
                    getSeq3tf().setText(otherseq);
                    break;
                case MiniSBE.PROBE_CLEAVABLE:
                    getProbePanel5().setSelectedType(assay1);
                    getPlpanel5().setSelectedValue(assay2);
                    getSeq3tf().setText(otherseq);
                    break;
                case MiniSBE.PROBE_PINPOINT:
                    getProbePanel5().setSelectedType(assay1);
                    getPinpointAddonPanel5().setSelectedValue(assay2);
                    getSeq3tf().setText(otherseq);
                    break;
                default:
                    break;
                }
            }else {
                getSeq3tf().setText(Helper.revcomplPrimer(seq));
                snp=Helper.complPrimer(snp);
                switch (assayType) {
                case MiniSBE.CLEAVABLE:
                    getPlpanel3().setSelectedValue(assay1);
                    break;
                case MiniSBE.PINPOINT:
                    getPinpointAddonPanel3().setSelectedValue(assay1);
                    break;
                case MiniSBE.PROBE:
                    getProbePanel3().setSelectedType(assay1);
                    getSeq5tf().setText(otherseq);
                    break;
                case MiniSBE.PROBE_CLEAVABLE:
                    getPlpanel3().setSelectedValue(assay2);
                    getSeq5tf().setText(otherseq);
                    break;
                case MiniSBE.PROBE_PINPOINT:
                    getProbePanel3().setSelectedType(assay1);
                    getPinpointAddonPanel3().setSelectedValue(assay2);
                    getSeq5tf().setText(otherseq);
                    break;
                default:
                    break;
                }
            }
            getSNPSelectorPanel().setSelectedNukleotides(snp);
            getPcrLenPanel().setText(prodlen);
            getMultiplexidPanel().setText("");
            getFiltersPanel().setText("");
            getFixedPrimerCB().setSelected(true);
    }
    /**
     * This method initializes stringEntryPanel
     *
     * @return biochemie.gui.StringEntryPanel
     */
    protected StringEntryPanel getFiltersPanel() {
        if (filtersPanel == null) {
            filtersPanel = new StringEntryPanel();
            filtersPanel.setLabel("Excluded primers");
            filtersPanel.setColumns(20);
            filtersPanel.setMaxLen(Integer.MAX_VALUE);
            filtersPanel.setResizeToStringLen(false);
            filtersPanel.setRekTooltip("Unpleasant primers. Not considered.");
        }
        return filtersPanel;
    }
    
    /**
     * @return
     */
    public String getFilters() {
        return getFiltersPanel().getText();
    }
    
    /**
     * @param oldfilters
     */
    public void setFilters(String f) {
        dirty();
        getFiltersPanel().setText(f);
    }
    /**
     * This method initializes fixedPrimerCB
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getFixedPrimerCB() {
        if (fixedPrimerCB == null) {
            fixedPrimerCB = new JCheckBox();
            fixedPrimerCB.setText("Fix");
            fixedPrimerCB.setToolTipText("If checked, length of 5' sequence will not be adjusted to the specified temperature and it will be used as is for multiplexing");
            fixedPrimerCB.addChangeListener((ChangeListener) cl);
        }
        return fixedPrimerCB;
    }
    
    /* (non-Javadoc)
     * @see biochemie.gui.MyPanel#setUnchanged()
     */
    public void setUnchanged() {
        super.setUnchanged();
        Component[] components=this.getComponents();
        for (int i = 0; i < components.length; i++) {
            if(components[i] instanceof MyPanel)
                ((MyPanel)components[i]).setUnchanged();

        }
    }
    
    /* (non-Javadoc)
     * @see biochemie.gui.MyPanel#hasChanged()
     */
    public boolean hasChanged() {
        if(super.hasChanged())
            return true;
        Component[] components=this.getComponents();
        for (int i = 0; i < components.length; i++) {
            if(components[i] instanceof MyPanel)
                if(((MyPanel)components[i]).hasChanged())
                    return true;
        }
        return false;
    }
    
    /**
     * This method initializes PLSelectorPanel	
     * 	
     * @return biochemie.gui.PLSelectorPanel	
     */    
    protected PLSelectorPanel getPlpanel3() {
        if (plpanel3 == null) {
            plpanel3 = new PLSelectorPanel();
            plpanel3.setTitle("Linker 3'");
        }
        return plpanel3;
    }
    
    public PrimerFactory createPrimerFactory(SBEOptions cfg, boolean rememberOutput) {
        if(!this.inputcontrollerL.isOkay() || !this.inputcontrollerR.isOkay())
            return null;
        String seq5=getSeq5tf().getText();
        String seq3=getSeq3tf().getText();
        if(seq5.length() == 0 && seq3.length()==0) //keine primer da
            return null;
        String id=getId();
        String snp=getSNPSelectorPanel().getSelectedNukleotides();
        String bautein5=getHairpin5SelectionPanel().getSelectedNukleotides();
        String bautein3=getHairpin3SelectionPanel().getSelectedNukleotides();
        int pcrlen=getPcrLenPanel().getText().length();
        try {
            pcrlen = Integer.parseInt(getPcrLenPanel().getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        String multiplexid = getMultiplexidPanel().getPBSequenceField().isEnabled()?getMultiplexidPanel().getText():"";
        String unwanted = getFiltersPanel().getText();
        boolean userGiven=getFixedPrimerCB().isSelected();
        switch (assayType) {
        case MiniSBE.CLEAVABLE:
            return getCleavablePrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        case MiniSBE.PROBE:
            return getProbePrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        case MiniSBE.PINPOINT:
            return getPinpointPrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        case MiniSBE.PROBE_PINPOINT:
            return getProbePinpointPrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        case MiniSBE.PROBE_CLEAVABLE:
            return getProbeCleavablePrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        default:
            break;
        }
        return null;
    }

    private PrimerFactory getProbeCleavablePrimerFactory(SBEOptions cfg, boolean rememberOutput, String id, String seq5, String seq3, String snp, String bautein5, String bautein3, int pcrlen, String multiplexid, String unwanted, boolean userGiven) {
        int assay5=getProbePanel5().getSelectedType();
        int assay3=getProbePanel3().getSelectedType();
        CleavablePrimerFactory cleave=getCleavablePrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        return new ProbePrimerFactory(cfg,id,seq5,snp,seq3,bautein5,bautein3,pcrlen,multiplexid,assay5,assay3,userGiven,unwanted,rememberOutput,cleave);
    }

    private PrimerFactory getProbePinpointPrimerFactory(SBEOptions cfg, boolean rememberOutput, String id, String seq5, String seq3, String snp, String bautein5, String bautein3, int pcrlen, String multiplexid, String unwanted, boolean userGiven) {
        int assay5=getProbePanel5().getSelectedType();
        int assay3=getProbePanel3().getSelectedType();
        PrimerFactory cleave=getPinpointPrimerFactory(cfg,rememberOutput,id,seq5,seq3,snp,bautein5,bautein3,pcrlen,multiplexid,unwanted,userGiven);
        return new ProbePrimerFactory(cfg,id,seq5,snp,seq3,bautein5,bautein3,pcrlen,multiplexid,assay5,assay3,userGiven,unwanted,rememberOutput,cleave);
    }

    public int getAssayType() {
        return assayType;
    }
    
}  //  @jve:decl-index=0:visual-constraint="65,28"
