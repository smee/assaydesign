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
        String seq=type==CROSSDIMER?other.getPrimerSeq():p.getPrimerSeq();
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
            if(cd.calc(params).length==0)
                return true;
        }
        return false;
    }

    public String getAsciiArt() {
        switch (type) {
        case HAIRPIN:
            return Helper.outputHairpin(p.getPrimerSeq(),pos-1,p.getPrimerSeq().length(), getEnthalpy());
        case HOMODIMER:
            return Helper.outputXDimer(p.getPrimerSeq(),p.getPrimerSeq(),p.getPrimerSeq().length() - pos,p.getPrimerSeq().length(), getEnthalpy());
        case CROSSDIMER:
            return Helper.outputXDimer(p.getPrimerSeq(),other.getPrimerSeq(),p.getPrimerSeq().length() - pos,Math.min(p.getPrimerSeq().length(),other.getPrimerSeq().length()), getEnthalpy());

        default:
            return "unknown type of sec.struk encountered.";
        }
    }

    public double getEnthalpy() {
        String match=null;
        switch (getType()) {
        case HAIRPIN:
            match=getBindingSeq(getPrimer().getPrimerSeq(),Helper.revcomplPrimer(getPrimer().getPrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(match)+Helper.LoopEnergy(getLoopLength(getPosFrom3(),match.length()))-1;
        case HOMODIMER:
            match=getBindingSeq(getPrimer().getPrimerSeq(),Helper.revcomplPrimer(getPrimer().getPrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(match)-1;
        case CROSSDIMER:
            match=getBindingSeq(getPrimer().getPrimerSeq(), Helper.revcomplPrimer(getCDPrimer().getPrimerSeq()),getPosFrom3());
            return Helper.cal_dG_secondaryStruct(Helper.complPrimer(match))-1;
        default:
            return 0;
        }
    }

    public boolean isIncompatible() {
        return incomp;
    }

}
