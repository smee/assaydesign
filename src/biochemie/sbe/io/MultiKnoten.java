/**
 *
 */
package biochemie.sbe.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import biochemie.sbe.SBEOptionsProvider;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.sbe.multiplex.MultiplexableFactory;

public class MultiKnoten implements MultiplexableFactory, Multiplexable{

        private List knoten;
        List multiplexables;
		String edgeReason;
		private SBEOptionsProvider cfg;
        private String givenId;

        public MultiKnoten(List knoten2, String givenid, SBEOptionsProvider cfg) {
            this.knoten=knoten2;
            this.cfg=cfg;
            this.givenId = givenid !=null?givenid:"";
        }

        public List getMultiplexables() {
            multiplexables=new ArrayList();
            for (int j = 0; j < knoten.size(); j++) {
                Object o = knoten.get(j);
                if(o instanceof MultiplexableFactory)//sollte n SBECandidate mit nur einem Primer sein, unsauber das
                    multiplexables.addAll(((MultiplexableFactory)o).getMultiplexables());
                else
                    multiplexables.add(o);//Multiplexable, keine Factory
            }
            List l=new ArrayList();
            if(multiplexables.size() != 0)//wenn es was zu multiplexen gibt
                l.add(this);//meld ich mich stellvertretend freiwillig :)
            return l;//sonst halt nich, weil ich hab nix mehr zu multiplexen
        }

        public String getCSVRow() {
            StringBuffer sb=new StringBuffer();
            for (Iterator it = knoten.iterator(); it.hasNext();) {
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
            sb.append(knoten.size());
            sb.append(']');
            return sb.toString();
        }
        /**
         * 
         */
        public boolean passtMit(Multiplexable o) {
            List other=new ArrayList();
            boolean differentGivenMultiplexes = false;
            if(o instanceof MultiKnoten) {
                other.addAll(((MultiKnoten)o).multiplexables);
//                differentGivenMultiplexes = givenId.length()!=0
//                && ((MultiKnoten)o).givenId.length()!=0
//                && !givenId.equalsIgnoreCase(((MultiKnoten)o).givenId);
            }else
            	other.add(o);
            for (Iterator it = multiplexables.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                for (Iterator iter = other.iterator(); iter.hasNext();) {
                    Multiplexable m2 = (Multiplexable) iter.next();
//                    if(differentGivenMultiplexes) {
//                        edgeReason = "differentGivenMultiplexIDs";//kein Test, sollen nicht zusammenkommen
//                        return false;
//                    }else
                        if(!m.passtMit(m2)){
                            edgeReason=m.getEdgeReason();
                            return false;
                        }
                }
            }
            return true;
        }

        public int maxPlexSize() {
            return cfg.getMaxPlex()-knoten.size()+1;
        }
        public String getEdgeReason(){
            return edgeReason;
        }
    }