/*
 * Created on 20.12.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptionsProvider;

/**
 * @author sdienst
 *
 */
public class UnwantedPrimerFilter extends AbstractKandidatenFilter {
    String[] type = new String[0];
    int[] len = new int[0];
    int[] pl = new int[0];
    private int count = 0;
    /**
     * @param cfg
     * @param unwanted
     */
    public UnwantedPrimerFilter(SBEOptionsProvider cfg, String unwanted) {
        super(cfg);
        count =0;
        
        if(unwanted == null || unwanted.length() == 0)
            return;
        
        StringTokenizer st = new StringTokenizer(unwanted);
        this.type = new String[st.countTokens()];
        this.len = new int[st.countTokens()];
        this.pl = new int[st.countTokens()];
        int i=0;
        while (st.hasMoreTokens()) {
            String tok=st.nextToken();
            String[] parts = tok.split("_");
            type[i]=parts[0];
            len[i]=Integer.parseInt(parts[1]);
            pl[i]=Integer.parseInt(parts[2]);
            i++;
        }
    }

    public void filter(List cand) {
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer p=(SBEPrimer) it.next();
            for (int i = 0; i < type.length; i++) {
                if(p.getType().equals(type[i]) && p.getSeq().length() == len[i] && p.getBruchstelle() == pl[i]) {
                    it.remove();
                    count++;
                    if(debug)                        
                        System.out.println("not considering "+p.getSeq()+", PL="+p.getBruchstelle()+", user doesn't like him!");
                }
                
            }
        }
    }

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return "Filtered by the user: ";
    }

}
