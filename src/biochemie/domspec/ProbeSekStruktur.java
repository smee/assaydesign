package biochemie.domspec;

import java.util.Iterator;
import java.util.List;

import biochemie.calcdalton.CalcDalton;
import biochemie.sbe.ProbePrimerFactory;
import biochemie.util.Helper;

public class ProbeSekStruktur extends SekStruktur {
    private boolean incomp;
    
    public ProbeSekStruktur(ProbePrimer p, ProbePrimer other, int pos) {
        super(p, other, pos);
        init();
    }

    public ProbeSekStruktur(ProbePrimer p, int type, int pos) {
        super(p, type, pos);
        init();
    }

    private void init(){
        String seq=type==CROSSDIMER?other.getCompletePrimerSeq():p.getCompletePrimerSeq();
        String unbindingPart=Helper.revcomplPrimer(seq.substring(0,seq.length()-pos));
        List addons=ProbePrimerFactory.generateAddons(((ProbePrimer)p).getAssayType(),
                unbindingPart.substring(1),
                unbindingPart.substring(0,1));
        incomp= !passtMitCalcDalton(addons);
    }
    private boolean passtMitCalcDalton(List addons) {
        CalcDalton cd=Helper.getCalcDalton();
        for (Iterator it = addons.iterator(); it.hasNext();) {
            String addon = (String) it.next();
            String[][] params=new String[][] {p.getCDParamLine(false),new String[2]};
            String seq=p.getPrimerSeq()+addon;
            params[1][0]=seq.substring(0,seq.length()-1);
            params[1][1]=seq.substring(seq.length()-1);
            if(cd.calc(params,false).length==0)
                return false;
        }
        return true;
    }

    public String getAsciiArt() {
        switch (type) {
        case HAIRPIN:
            return Helper.outputHairpin(p.getCompletePrimerSeq(),pos-1,p.getCompletePrimerSeq().length(), getEnthalpy());
        case HOMODIMER:
            return Helper.outputXDimer(p.getCompletePrimerSeq(),p.getCompletePrimerSeq(),p.getCompletePrimerSeq().length() - pos,p.getCompletePrimerSeq().length(), getEnthalpy());
        case CROSSDIMER:
            return Helper.outputXDimer(p.getCompletePrimerSeq(),other.getCompletePrimerSeq(),p.getCompletePrimerSeq().length() - pos,Math.min(p.getCompletePrimerSeq().length(),other.getCompletePrimerSeq().length()), getEnthalpy());

        default:
            return "unknown type of sec.struk encountered.";
        }
    }

    public double getEnthalpy() {
        String match=null;
        switch (getType()) {
        case HAIRPIN:
            match=getBindingSeq(getPrimer().getCompletePrimerSeq(),Helper.revcomplPrimer(getPrimer().getCompletePrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(match)+Helper.LoopEnergy(getLoopLength(getPosFrom3(),match.length()))-1;
        case HOMODIMER:
            match=getBindingSeq(getPrimer().getCompletePrimerSeq(),Helper.revcomplPrimer(getPrimer().getCompletePrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(match)-1;
        case CROSSDIMER:
            match=getBindingSeq(getPrimer().getCompletePrimerSeq(), Helper.revcomplPrimer(getCDPrimer().getCompletePrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(Helper.complPrimer(match))-1;
        default:
            return 0;
        }
    }

    public boolean isIncompatible() {
        return incomp;
    }

}
