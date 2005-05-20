/*
 * Created on 20.12.2004
 *
 */
package biochemie.sbe.filter;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import biochemie.domspec.SBEPrimer;
import biochemie.sbe.SBEOptions;

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
    public UnwantedPrimerFilter(SBEOptions cfg, String unwanted) {
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
            if(parts[1].equals("*"))
                len[i]=-1;
            else
                len[i]=Integer.parseInt(parts[1]);
            if(parts[2].equals("*"))
                pl[i]=-1;
            else
                pl[i]=Integer.parseInt(parts[2]);
            i++;
        }
    }

    public void filter(List cand) {
    	StringBuffer sb=new StringBuffer("Primers filtered by user:\n ");
        for (Iterator it= cand.iterator(); it.hasNext();) {
            SBEPrimer p=(SBEPrimer) it.next();
            for (int i = 0; i < type.length; i++) {
                if((p.getType().equals(type[i]) || type[i].equals("*")) &&
                   (p.getSeq().length() == len[i] || len[i] < 0) &&
                   (p.getBruchstelle() == pl[i] || pl[i] < 0)){
                    it.remove();
                    count++;
                    sb.append(p.getSeq()+", PL="+p.getBruchstelle());
                    sb.append("\n");
                }
            }
        }
        System.out.println(sb);
    }

    public int rejectedCount() {
        return count;
    }

    public String rejectReason() {
        return "Filtered by the user: ";
    }

}
