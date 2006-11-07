/*
 * Created on 30.11.2004
 *
 */
package biochemie.domspec;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.pcr.modules.HairpinAnalysis;
import biochemie.pcr.modules.HomoDimerAnalysis;
import biochemie.sbe.SecStrucOptions;

/**
 * @author Steffen Dienst
 *
 */
public class SekStrukturFactory {

	public static Set getSecStruks(Primer p, SecStrucOptions cfg){
        HairpinAnalysis hpa= getHairpinAnalysisInstance(cfg);
        HomoDimerAnalysis hda= getHomoDimerAnalysisInstance(cfg);
        String seq=p.getCompletePrimerSeq();
        
        Set poshairpins= hpa.getHairpinPositions(seq);
        Set poshomodimer= hda.getHomoDimerPositions(seq);
        Set sekstrukts=new HashSet(poshairpins.size()+poshomodimer.size());
        
        for (Iterator it= poshairpins.iterator(); it.hasNext();) {
            Integer pos= (Integer)it.next();
            sekstrukts.add(createSekStruktur(p,SekStruktur.HAIRPIN,pos.intValue()));
        }
        for (Iterator it= poshomodimer.iterator(); it.hasNext();) {
            Integer pos= (Integer)it.next();
            sekstrukts.add(createSekStruktur(p,SekStruktur.HOMODIMER,pos.intValue()));
        }
        return sekstrukts;
	}
	private static SekStruktur createSekStruktur(Primer p, int type, int pos) {
	    if(p instanceof CleavablePrimer)
	        return new CleavableSekStruktur((CleavablePrimer)p,type,pos);
	    else if(p instanceof ProbePrimer)
	        return new ProbeSekStruktur((ProbePrimer)p,type,pos);
	    else
	        return new SekStruktur(p,type,pos);
	}
    public static Set getCrossdimer(Primer p, Primer other, SecStrucOptions cfg){
        CrossDimerAnalysis cda = getCrossDimerAnalysisInstance(cfg);
        return getCrossdimer(p,other,cda);
	}
	
	public static HairpinAnalysis getHairpinAnalysisInstance(SecStrucOptions cfg){
		return new HairpinAnalysis(cfg.getHairpinWindowsizes(),cfg.getHairpinMinbinds(),"false");
	}
    public static HomoDimerAnalysis getHomoDimerAnalysisInstance(SecStrucOptions cfg){
		return new HomoDimerAnalysis(cfg.getHomodimerWindowsizes(),cfg.getHomodimerMinbinds(),"false");
	}
    public static CrossDimerAnalysis getCrossDimerAnalysisInstance(SecStrucOptions cfg){
		return new CrossDimerAnalysis(cfg.getCrossDimerWindowsizes(),cfg.getCrossdimerMinbinds(),"false");
	}
    /**
     * @param primer
     * @param other
     * @param cda
     * @return
     */
    public static Set getCrossdimer(Primer p, Primer other, CrossDimerAnalysis cda) {
        String pseq = p.getCompletePrimerSeq();
        String oseq =other.getCompletePrimerSeq();

        Set positions= cda.getCrossDimerPositions(pseq,oseq);
        Set sek=new HashSet();
        
        for (Iterator it = positions.iterator(); it.hasNext();) {
            Integer pos = (Integer) it.next();
            sek.add(createCrossdimer(p,other,pos.intValue()));
        }
        return sek;
    }
    private static SekStruktur createCrossdimer(Primer p, Primer other, int pos) {
        if(p instanceof CleavablePrimer && other instanceof CleavablePrimer)
            return new CleavableSekStruktur((CleavablePrimer)p,(CleavablePrimer)other,pos);
        else if(p instanceof ProbePrimer && other instanceof ProbePrimer)
            return new ProbeSekStruktur((ProbePrimer)p,(ProbePrimer)other,pos);
        else
            return new SekStruktur(p,other,pos);
    }
}
