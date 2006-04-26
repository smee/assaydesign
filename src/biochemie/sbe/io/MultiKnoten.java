/**
 *
 */
package biochemie.sbe.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org._3pq.jgrapht.Edge;

import biochemie.pcr.matcher.MyDefaultEdge;
import biochemie.sbe.CleavablePrimerFactory;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.sbe.multiplex.MultiplexableFactory;
import biochemie.util.edges.MyUndirectedEdge;
/**
 * Vereinigung mehrerer SBECandidates, die alle in einen Multiplex sollen.
 * @author sdienst
 *
 */
public class MultiKnoten implements MultiplexableFactory, Multiplexable{

        private final List factories;
        List multiplexables;
        Collection edgecol;
        private final String givenId;
        final int realSize;

        public MultiKnoten(List sbec, String givenid) {
            this.factories=sbec;
            this.realSize=factories.size();
            if(givenid == null || givenid.length()==0)
                throw new IllegalArgumentException("Given multiplexid \""+givenid+"\" isn't valid!");
            this.givenId = givenid !=null?givenid:"";
        }
        public MultiKnoten(Collection multiplexables) {
            this.multiplexables=new ArrayList(multiplexables);
            givenId="multiknoten";
            factories=null;
            this.realSize=multiplexables.size();
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
            sb.append(realSize);
            sb.append(']');
            return sb.toString();
        }
        /**
         * 
         */
        public boolean passtMit(Multiplexable o) {
            edgecol=new LinkedList();
            if(o instanceof MultiKnoten) {
                boolean differentGivenMultiplexes = !givenId.equalsIgnoreCase(((MultiKnoten)o).givenId);
                if(differentGivenMultiplexes) {
                    edgecol.add(new DiffGivenMIDEdge(this,o,givenId));//kein Test, sollen nicht zusammenkommen
                    return false;
                }
            }
            
            List other=o.getIncludedElements();
            boolean flag=true;
            for (Iterator it = multiplexables.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                for (Iterator iter = other.iterator(); iter.hasNext();) {
                    Multiplexable m2 = (Multiplexable) iter.next();
                        if(!m.passtMit(m2)){
                            edgecol.add(new MyDefaultEdge(this,o));//TODO die sache mit den kanten nochmal in ruhe durchdenken
                            flag=false;
                        }
                }
            }
            return flag;
        }

        public int realSize() {
            return realSize;
        }


        public List getIncludedElements() {
            return new ArrayList(multiplexables);
        }
        public List getSBECandidates() {
            List ret = new LinkedList();
            for (Iterator it = factories.iterator(); it.hasNext();) {
                MultiplexableFactory mf = (MultiplexableFactory) it.next();
                if(mf instanceof CleavablePrimerFactory)
                    ret.add(mf);
                else if(mf instanceof MultiKnoten)
                    ret.addAll(((MultiKnoten)mf).getSBECandidates());
                else
                    throw new IllegalArgumentException("FEHLER im Programm: Liste darf nur SBECandidates oder MultiKnoten enthalten!");
            }
            return ret;
        }

        public Collection getLastEdges() {
            return edgecol;
        }
        public String toString() {
            return getIncludedElements().toString();
        }
        private static class DiffGivenMIDEdge extends MyUndirectedEdge{
            private final String mid;
            public DiffGivenMIDEdge(Object sourceVertex, Object targetVertex, String mid) {
                super(sourceVertex, targetVertex);
                this.mid=mid;
            }
            public String toString() {
                return "differentGivenMultiplexIDs";
            }
            public String matchString() {
                return mid;
            }
        }
        public String[] getCsvheader() {
            return null;
        }
    }