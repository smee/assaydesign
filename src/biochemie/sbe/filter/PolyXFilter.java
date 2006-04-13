/*
 * Created on 20.02.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;

import biochemie.domspec.Primer;
import biochemie.sbe.SBEOptions;

/**
 *
 * @author Steffen
 *
 */
public class PolyXFilter extends AbstractKandidatenFilter {
    private int polyX;
    public PolyXFilter(SBEOptions cfg){
    	super(cfg);
        this.polyX=cfg.getPolyX();
    }
    /**
     * Filtert alle Seq., in denen ein Nukleotid mehr als <code>polyX</code>-mal 
     * hintereinander vorkommt.
     * @see KandidatenFilter#filter(List)
     */
    public void filter(List cand) {
    	StringBuffer sb=new StringBuffer("Primers exceeding polyX:\n");
        for (Iterator it= cand.iterator(); it.hasNext();) {
            Primer p= (Primer) it.next();
            String seq=p.getSeq();
            int counter=0;
            char c=0;
            boolean tracingrun=false;
            for(int i=0;i<seq.length();i++){
                if(!tracingrun){
                    c=seq.charAt(i);
                    tracingrun=true;
                    counter=1;
                }else{
                    if(c==seq.charAt(i))
                        counter++;
                    else{
                        tracingrun=false;
                        i--;
                    }
                }
            }
            if(counter>polyX){
                it.remove();
                count++;                        	
                sb.append(getPrimerDescription(p)).append(", ");
                sb.append(markRed("polyX="+counter));
                sb.append("\n");
            }              
        }
        System.out.println(sb);
    }
    private int count=0;
    private final String reason="out of PolyX: ";

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return reason;
    }
}
