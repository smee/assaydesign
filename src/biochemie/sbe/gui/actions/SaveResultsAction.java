/*
 * Created on 18.01.2005
 *
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
            String path=file.getAbsolutePath();
            if(!path.endsWith(".csv") && !path.endsWith(".CSV"))
                path += ".csv";
            file=new File(path);
            SBEPrimerReader.writeSBEResults(file.getAbsolutePath(),sbec);
        }
    }

}
