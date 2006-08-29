package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.calcdalton.CalcDalton;
import biochemie.domspec.Primer;
import biochemie.sbe.SBEOptions;
import biochemie.util.Helper;

public class ForbiddenPeakDistanceFilter extends AbstractKandidatenFilter {

    private CalcDalton cd;

    public ForbiddenPeakDistanceFilter(SBEOptions cfg) {
        super(cfg);
        cd=Helper.getCalcDalton(cfg);
        reason="invalid peak mass diff :";
    }

    public void filter(List cand) {
        StringBuffer sb=new StringBuffer("Assay has invalid mass peak differences:\n");
        for (Iterator it = cand.iterator(); it.hasNext();) {
            Primer primer = (Primer) it.next();
            double[] masses=cd.getMasses(primer);
            if(cd.invalidPeakDiffIn(masses)) {
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
