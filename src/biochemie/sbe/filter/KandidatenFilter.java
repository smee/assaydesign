package biochemie.sbe.filter;
import java.util.List;

/*
 * Created on 20.02.2004
 *
 */
/**
 *
 * @author Steffen
 *
 */
public interface KandidatenFilter {
    /**
     * Filtert eine Liste von Kandidaten, i.e. Strings mit DNA-Sequenzen nach 
     * irgendeinem Kriterium. Die Liste muss mutable sein. Sie wird direkt verändert.
     * @param cand
     */
    public void filter(List cand);
    /**
     * Anzahl der verworfenen Kandidaten
     * @return
     */
    public int rejectedCount();
    /**
     * Grund fuer das Verwerfen eines Kandidaten (prinzipieller Grund)
     * @return
     */
    public String rejectReason();
}
