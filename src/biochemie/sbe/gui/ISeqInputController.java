package biochemie.sbe.gui;

public interface ISeqInputController {

    boolean isOkay();

    void setOtherController(ISeqInputController inputcontrollerR);

    void setEnabled(boolean b);

}
