/*
 * Created on 18.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.sbe.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.KeyStroke;

import biochemie.sbe.io.SBEPrimerReader;
import biochemie.util.FileSelector;
import biochemie.util.MyAction;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveResultsAction extends MyAction {
    private List sbec;
    /**
     * @param model
     */
    public SaveResultsAction(List sbec) {
        super("Save results","save result table to file"
                ,SaveResultsAction.class.getClassLoader().getResource("images/save.gif")
                ,KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK));
        this.sbec = sbec;;
    }

    public void actionPerformed(ActionEvent e) {
        File file = FileSelector.getUserSelectedFile(null,"Save result",FileSelector.CSV_FILEFILTER,FileSelector.SAVE_DIALOG);
        if(file != null) {
            SBEPrimerReader.writeSBEResults(file.getAbsolutePath(),sbec);
        }
    }

}
