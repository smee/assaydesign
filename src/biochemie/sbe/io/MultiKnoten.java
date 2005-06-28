/**
 *
 */
package biochemie.sbe.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import biochemie.sbe.SBECandidate;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.sbe.multiplex.MultiplexableFactory;
/**
 * Vereinigung mehrerer SBECandidates, die alle in einen Multiplex sollen.
 * @author sdienst
 *
 */
public class MultiKnoten implements MultiplexableFactory, Multiplexable{

        private final List factories;
        List multiplexables;
		String edgeReason;
        private final String givenId;

        public MultiKnoten(List sbec, String givenid) {
            this.factories=sbec;
            if(givenid == null || givenid.length()==0)
                throw new IllegalArgumentException("Given multiplexid \""+givenid+"\" isn't valid!");
            this.givenId = givenid !=null?givenid:"";
        }

        public List getMultiplexables() {
            multiplexables=new ArrayList();
            for (int j = 0; j < factories.size(); j++) {
                Object o = factories.get(j);
                multiplexables.addAll(((MultiplexableFactory)o).getMultiplexables());
            }
            List l=new ArrayList();
            if(multiplexables.size() != 0)//wenn es was zu multiplexen gibt
                l.add(this);//meld ich mich stellvertretend freiwillig :)
            return l;//sonst halt nich, weil ich hab nix mehr zu multiplexen
        }

        public String getCSVRow() {
            StringBuffer sb=new StringBuffer();
            for (Iterator it = factories.iterator(); it.hasNext();) {
                MultiplexableFactory mf = (MultiplexableFactory) it.next();
                sb.append(mf.getCSVRow());
                sb.append('\n');
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }

        public void setPlexID(String s) {
            for (Iterator it = multiplexables.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                m.setPlexID(s);
            }
        }

        public String getName() {
            StringBuffer sb = new StringBuffer("[");
            sb.append("gegebenerKnoten, Groesse ");
            sb.append(factories.size());
            sb.append(']');
            return sb.toString();
        }
        /**
         * 
         */
        public boolean passtMit(Multiplexable o) {
            if(o instanceof MultiKnoten) {
                boolean differentGivenMultiplexes = !givenId.equalsIgnoreCase(((MultiKnoten)o).givenId);
                if(differentGivenMultiplexes) {
                    edgeReason = "differentGivenMultiplexIDs";//kein Test, sollen nicht zusammenkommen
                    return false;
                }
            }
            
            List other=o.getIncludedElements();
            for (Iterator it = multiplexables.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                for (Iterator iter = other.iterator(); iter.hasNext();) {
                    Multiplexable m2 = (Multiplexable) iter.next();
                        if(!m.passtMit(m2)){
                            edgeReason=m.getEdgeReason();
                            return false;
                        }
                }
            }
            return true;
        }

        public int realSize() {
            return factories.size();
        }
        public String getEdgeReason(){
            return edgeReason;
        }

        public List getIncludedElements() {
            return new ArrayList(multiplexables);
        }
        public List getSBECandidates() {
            List ret = new LinkedList();
            for (Iterator it = factories.iterator(); it.hasNext();) {
                MultiplexableFactory mf = (MultiplexableFactory) it.next();
                if(mf instanceof SBECandidate)
                    ret.add(mf);
                else if(mf instanceof MultiKnoten)
                    ret.addAll(((MultiKnoten)mf).getSBECandidates());
                else
                    throw new IllegalArgumentException("FEHLER im Programm: Liste darf nur SBECandidates oder MultiKnoten enthalten!");
            }
            return ret;
        }
    }