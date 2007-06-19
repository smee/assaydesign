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
        reason="out of PolyX: ";
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
            String seq=p.getPrimerSeq();
            int counter=0, max=0;
            char c=0;
            boolean tracingrun=false;
            for(int i=0;i<seq.length();i++){
                if(!tracingrun){
                	if(counter>max)
                		max=counter;
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
            if(max>polyX){
                it.remove();
                count++;                        	
                sb.append(getPrimerDescription(p)).append(", ");
                sb.append(markRed("polyX="+max));
                sb.append("\n");
            }              
        }
        System.out.println(sb);
    }
}
