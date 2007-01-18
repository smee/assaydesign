package biochemie.sbe.gui;


public class GeneralSeqInputController extends AbstractSeqInputController{

    private SBECandidatePanel panel;

    public GeneralSeqInputController(SBECandidatePanel panel, int minlen, boolean isLeft) {
        super(panel,minlen,isLeft);
        this.panel=panel;
    }

    protected void handleSeqChange() {
        String text=left.getText();
        if(text.length()==0 || text.length()>=minlen){
            fixedcb.setEnabled(true);
            setToolTipAndBorder(text,false);
        }else{
            fixedcb.setEnabled(false);
            setToolTipAndBorder("Sequence too short! Enter at least "+minlen+" characters.",true);
        }
    }

    public void setEnabled(boolean b){
        super.setEnabled(b);
        if(isLeft)
            panel.getSeq5AssayDataComponent().setEnabled(b);
        else
            panel.getSeq3AssayDataComponent().setEnabled(b);
    }
}
