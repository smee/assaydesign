/*
 * Created on 11.01.2005
 *
 */
package biochemie.sbe.gui;

import java.io.File;

import javax.swing.AbstractAction;

/**
 * @author sdienst
 *
 */
public abstract class FileAction extends AbstractAction {
    private File _lastdir=new File(".");

    protected File getLastUsedDirectory() {
        return _lastdir;
    }
    protected void setLastUsedDirectory(File f) {
        if(f.isFile())
            _lastdir=f;
        else
            _lastdir=f.getParentFile();
    }

}
