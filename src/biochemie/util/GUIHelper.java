package biochemie.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class GUIHelper {

    public static void center(JDialog dialog, JFrame parent) {
        Dimension dim = dialog.getPreferredSize();
        Dimension frameSize;
        int x;
        int y;
        if (parent == null) {
            frameSize = Toolkit.getDefaultToolkit().getScreenSize();
            x = 0;
            y = 0;
        }
        else {
            frameSize = parent.getSize();
            Point loc = parent.getLocation();
            x = loc.x;
            y = loc.y;
        }

        x += (frameSize.width - dim.width) / 2;
        y += (frameSize.height - dim.height) / 2;

        dialog.setLocation(x, y);
    }
}
