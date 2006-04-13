/*
 * Created on 30.11.2004
 *
 */
package biochemie.domspec;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
        String seq=p.getSeq();
        
        List poshairpins= hpa.getHairpinPositions(seq);
        List poshomodimer= hda.getHomoDimerPositions(seq);
        Set sekstrukts=new HashSet(poshairpins.size()+poshomodimer.size());
        
        for (Iterator it= poshairpins.iterator(); it.hasNext();) {
            Integer pos= (Integer)it.next();
            if(p instanceof SBEPrimer)
            	sekstrukts.add(new SBESekStruktur((SBEPrimer)p,SekStruktur.HAIRPIN,pos.intValue()));
            else
            	sekstrukts.add(new SekStruktur(p,SekStruktur.HAIRPIN,pos.intValue()));
        }
        for (Iterator it= poshomodimer.iterator(); it.hasNext();) {
            Integer pos= (Integer)it.next();
            if(p instanceof SBEPrimer)
            	sekstrukts.add(new SBESekStruktur((SBEPrimer)p,SekStruktur.HOMODIMER,pos.intValue()));
            else
            	sekstrukts.add(new SekStruktur(p,SekStruktur.HOMODIMER,pos.intValue()));
        }
        return sekstrukts;
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
        String pseq = p.getSeq();
        String oseq =other.getSeq();

        List positions= cda.getCrossDimerPositions(pseq,oseq);
        Set sek=new HashSet();
        
        for (Iterator it = positions.iterator(); it.hasNext();) {
            Integer pos = (Integer) it.next();
            if(p instanceof SBEPrimer && other instanceof SBEPrimer)
                sek.add(new SBESekStruktur((SBEPrimer)p,(SBEPrimer)other,pos.intValue()));
            else
                sek.add(new SekStruktur(p,other,pos.intValue()));
        }
        return sek;
    }
}
