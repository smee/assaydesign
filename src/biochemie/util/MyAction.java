/*
 * Created on 18.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.util;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class MyAction extends AbstractAction {

    public MyAction(String name, String tooltip, URL iconurl,KeyStroke key) {
        putValue(ACCELERATOR_KEY,key);
        putValue(NAME,name);
        putValue(SHORT_DESCRIPTION,tooltip);
        if( iconurl != null ){
            Icon icon=new ImageIcon(iconurl);
            putValue(Action.SMALL_ICON,icon);
        }
    }
}
