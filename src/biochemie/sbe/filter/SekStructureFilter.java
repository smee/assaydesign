/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;

import biochemie.domspec.Primer;
import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;
import biochemie.sbe.SBEOptions;

/**
 *
 * @author Steffen
 *
 */
public class SekStructureFilter extends AbstractKandidatenFilter  {


    
    public SekStructureFilter(SBEOptions cfg){
    	super(cfg);
    }
   
    public void filter(List cand) {
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer p=(SBEPrimer) it.next();

            if(p.numOfHHSekStruks() == 0)
                continue;
            if(hatMehrereInkompatibleHairpins(p)){
                it.remove();
                count++;
				if(debug)
                	System.out.println("not considering "+p.getSeq()+", PL="+p.getBruchstelle()+", too many incompatible secondary structures!");
            }else {//keine Seq. hat mehrere, jetzt schauen, ob nur eine inkomp. existiert, die nicht verhindert werden kann
                
                if(hatNichtVerhinderbareSekStruks(p)) {
                    it.remove();
                    count++;
                    if(debug)                        
                        System.out.println("not considering "+p.getSeq()+", PL="+p.getBruchstelle()+", can't avoid incomp. hairpin!");
                }
            }
        }
    }
    /**
     * Testet, ob ein Primer mehrere inkompatible Hairpins hat, genauer: Mehr als einen Hintergrund: Ein inkompatibler Hairpin bedeutet,
     * dass der Hairpin ein Nukleotid einbaut, welches im SNP vorkommt. Damit w�rde das Ergebnis verf�lscht. Man kann das entsch�rfen, 
     * indem ein fester Photolinker an dieser Stelle eingebaut wird. Beispiel:<br>
     * ACGTGTTGTGA-<br>
     * &nbsp;&nbsp;&nbsp;   ||||&nbsp;&nbsp;&nbsp;&nbsp;| und SNP= C,T<br>
     *    ACAATTTC-<br>
     * In diesem Beispiel w�rde der Hairpin ein C einbauen, also inkompatibler Hairpin. Wird verhindert durch Photolinker an Stelle 16
     * (von 3' aus gez�hlt, Start bei 1). Wenn aber mehrere solche Hairpins da sind, kann nix entsch�rft werden, und der Kandidat fliegt raus.
     * @param cutprimer
     * @param hair
     * @return
     */
    private boolean hatMehrereInkompatibleHairpins(Primer p) {
        Integer count=(Integer)Algorithms.inject(p.getSecStrucs().iterator(),new Integer(0),new BinaryFunction() {
            public Object evaluate(Object left, Object right) {
                int inc= ((SBESekStruktur)right).isIncompatible()?1:0;
                return new Integer(((Integer)left).intValue() + inc);
            }            
        });
        return count.intValue() > 1;
    }
    private boolean hatNichtVerhinderbareSekStruks(Primer p) {
        Integer count=(Integer)Algorithms.inject(p.getSecStrucs().iterator(),new Integer(0),new BinaryFunction() {
            public Object evaluate(Object left, Object right) {
                int inc= ((SBESekStruktur)right).isIncompatible() && !((SBESekStruktur)right).isVerhindert() ? 1 : 0;
                return new Integer(((Integer)left).intValue() + inc);
            }            
        });
        return count.intValue() > 1;
    }
    private int count=0;
    private final String reason="unavoidable sec.struks: ";

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return reason;
    }
}
