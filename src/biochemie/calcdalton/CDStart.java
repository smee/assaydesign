/*
 * Created on 14.02.2004
 *
 */
package biochemie.calcdalton;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import biochemie.calcdalton.gui.SBEGui;

/**
 *
 * @author Steffen
 *
 */
public class CDStart {
    public static void main(String[] args) {
        if(1 <= args.length) {
            if(args[0].equals("-debug")) {
                CalcDalton.debug=true;
            }
            else if(args[0].equals("-progress")) {
                CalcDalton.progress=true;
            }
            else if(args[0].equals("-all")) {
                CalcDalton.progress=true;
                CalcDalton.debug=true;
            }
            else {
                System.out.println("\nSyntax:\n-------\n\njava -jar CalcDalton.jar [option]\n\noptions:\n--------\n" +
                    "-debug\t\tzeige interne Infos zur Berechnung\n-progress\tzeige Fortschritt numerisch an\n" +
                    "-all\t\tzeige -debug und -progress");
                System.exit(0);
            }
        }
//        if(!Boolean.getBoolean("DEBUG"))
//            LogStdStreams.initializeErrorLogging("calcdalton.log", "---------Program started: " + new Date()+" -----------", true, true);
//        else
            System.out.println("using output to console...");
        try {
            UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.Plastic3DLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
        }
        SBEGui gui= SBEGui.getInstance();
    }
}
