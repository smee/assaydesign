/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import biochemie.domspec.Primer;
import biochemie.sbe.SBEOptions;
import biochemie.util.Helper;

/**
 *
 * @author Steffen
 *
 */
public class LaengenFilter extends AbstractKandidatenFilter {

    private final int len;
    
    public LaengenFilter(SBEOptions cfg){
    	super(cfg);
        int maxpl=Helper.findMaxIn(cfg.getPhotolinkerPositions());
        this.len=Math.max(cfg.getMinCandidateLen(),maxpl);
        reason="primer too short: ";
    }
    /**
     * Filtert alle Seq., deren Laenge kleiner einer Laenge len ist.
     */
    public void filter(List cand) {
    	StringBuffer sb=new StringBuffer("Primers too short:\n");
        for (Iterator it= cand.iterator(); it.hasNext();) {
            Primer  p = (Primer) it.next();
            if(p.getPrimerSeq().length()<len){
                it.remove();
                count++;
                sb.append(getPrimerDescription(p)).append(", ");
                sb.append(markRed("length="+p.getPrimerSeq().length()+"bp"));
                sb.append("\n");
            }
        }
        System.out.println(sb);
    }
}
