/*
 * Created on 14.02.2004
 *
 */
package biochemie.calcdalton;

import java.util.Date;

import biochemie.calcdalton.gui.SBEGui;
import biochemie.util.LogStdStreams;

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
        LogStdStreams.initializeErrorLogging("calcdalton.log", "---------Program started: " + new Date()+" -----------", true, true);
        SBEGui gui= SBEGui.getInstance();
    }
}
