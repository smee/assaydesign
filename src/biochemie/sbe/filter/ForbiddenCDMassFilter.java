package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.calcdalton.CalcDalton;
import biochemie.domspec.Primer;
import biochemie.sbe.SBEOptions;
import biochemie.util.Helper;

public class ForbiddenCDMassFilter extends AbstractKandidatenFilter{
    CalcDalton cd;
    public ForbiddenCDMassFilter(SBEOptions cfg) {
        super(cfg);
        cd=Helper.getCalcDalton(cfg);
        reason="primer or product is within prohibited mass range: ";
    }

    public void filter(List cand) {
        StringBuffer sb=new StringBuffer("Primer or product is within prohibited mass range:\n");
        for (Iterator it = cand.iterator(); it.hasNext();) {
            Primer primer = (Primer) it.next();
            double[] masses=cd.getMasses(primer);
            if(cd.invalidMassesIn(masses)) {
                it.remove();
                count++;
                sb.append(getPrimerDescription(primer));
                sb.append(", ");
                sb.append(markRed("masses="+Helper.toString(masses)));
                sb.append("\n");
            }
        }
        System.out.println(sb);
    }



}
