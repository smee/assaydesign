package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.calcdalton.CalcDalton;
import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;
import biochemie.util.Helper;

public class ForbiddenCDMassFilter extends AbstractKandidatenFilter{
    CalcDalton cd;
    public ForbiddenCDMassFilter(SBEOptions cfg) {
        super(cfg);
        cd=Helper.getCalcDalton(cfg);
    }

    public void filter(List cand) {
        StringBuffer sb=new StringBuffer("Primer or product is within prohibited mass range:\n");
        for (Iterator it = cand.iterator(); it.hasNext();) {
            SBEPrimer primer = (SBEPrimer) it.next();
            String[] params=SBEPrimer.getCDParamLine(primer);
            double[] masses=cd.calcSBEMass(params,primer.getBruchstelle());
            if(cd.invalidMassesIn(masses)) {
                it.remove();
                count++;
                sb.append(primer.getSeq()+", masses="+Helper.toString(masses));
                sb.append("\n");
            }
        }
        System.out.println(sb);
    }

    private int count=0;
    private final String reason="primer or product is within prohibited mass range: ";

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return reason;
    }

}
