/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.sbe.multiplex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.UndirectedGraph;

import biochemie.domspec.Primer;
import biochemie.domspec.SekStruktur;
import biochemie.domspec.SekStrukturFactory;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.MiniSBE;
import biochemie.sbe.SBEOptions;
import biochemie.util.edges.MyUndirectedEdge;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class Multiplexer {
	private static int plexnr=1;
    private static boolean stopped;
    protected final boolean debug;
    protected SBEOptions cfg;

	public Multiplexer( SBEOptions cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();
	}
	/**
     * Methode, die einer Menge von Multiplexables Multiplex-IDs zuweist.
     * Erwartet eine Liste von @link SBEPrimer
	 * @param sbec
	 */
	public abstract void findMultiplexes(UndirectedGraph graph);

    /**
     * Liefert einen String, der als ID eines einzelnen Multiplexes dient.
     * @return
     */
    protected String getNextMultiplexID() {
        return 'M' + MiniSBE.getDatum() + '-' + plexnr++;
    }

    /**
     * Tags the multiplexables with an unique identifier.
     * @param multi
     */
    protected void giveMultiplexIDTo(Set multi) {
        String plexid=getNextMultiplexID();
        if(debug) System.out.println("New multiplex "+plexid+" for :");
        for (Iterator iter = multi.iterator(); iter.hasNext();) {
            Multiplexable struc = (Multiplexable) iter.next();
            if(debug) System.out.println(struc);
            String crntPlexID=struc.getPlexID();
            if(crntPlexID==null || crntPlexID.length()==0)
                struc.setPlexID(plexid);
        }
    }
    /**
     * Testet paarweise auf Crossdimer. Wenn kompatible gefunden werden,
     * wird jeweils genau dann ein neuer MultiKnoten erzeugt, wenn
     * es keinen anderen Grund gibt, die beiden Primer in einen Multiplex zu lassen.
     * Dieser macht dann spaeter Kanten mit den beiden Ursprungsprimern und allen anderen
     * Primern, die das von diesem CD eingebaute Nukleotid im SNP haben. Auf diese Weise
     * gibt es in den resultierenden Mulitplexen nur einen der drei Knoten: Primer A, Primer B
     * oder beide, dann wurden aber die eingebauten Nukleotide beruecksichtigt.
     * Macht also allCrossdimersAreEvil ueberfluessig :)
     * @param sbep List von SBEPrimern
     * @return
     */
    public static  Set getEnhancedPrimerList(Set sbep,SBEOptions cfg){
        System.out.println("Creating pseudoprimers because of comp. crossdimers. Comparing "+sbep.size()+" primers..");
    	Set result=new HashSet(sbep);
    	Multiplexable[] mults=(Multiplexable[]) sbep.toArray(new Multiplexable[sbep.size()]);
    	for (int i = 0; i < mults.length; i++) {
            List primers1=getAllPrimers(mults[i]);
			for (int j =i+1; j < mults.length; j++) {
			    List primers2=getAllPrimers(mults[j]);
                if(!passtMitKompatiblenCD(primers1, primers2))
					continue;//passt eh aus anderen gruenden nicht miteinander
				Collection sekstruks=getCrossdimersOf(primers1, primers2,SekStrukturFactory.getCrossDimerAnalysisInstance(cfg.getSecStrucOptions()));
				
				Set kompchars=new HashSet();
				for (Iterator it = sekstruks.iterator(); it.hasNext();) {//TODO what about probeprimers?
					SekStruktur cd = (SekStruktur) it.next();
					if(!cd.isIncompatible())
						kompchars.add(new Character(cd.bautEin().charAt(0)));
				}
				if(kompchars.size()!=0){
					String bautein="";
					for (Iterator it = kompchars.iterator(); it.hasNext();) {
						Character c = (Character) it.next();
						bautein+=c.charValue();
					}
					result.add(new SBEPrimerProxy(primers1, primers2,bautein));
				}
			}
		}
    	
    	
    	return result;
    }
    
    public static List getAllPrimers(Multiplexable m) {
        List result=new LinkedList();
        LinkedList queue=new LinkedList();
        queue.add(m);
        while(!queue.isEmpty()) {
            Multiplexable mult = (Multiplexable) queue.removeFirst();
            if(mult instanceof Primer)
                result.add(mult);
            else
                queue.addAll(mult.getIncludedElements());
        }
        return result;
    }
    private static List getCrossdimersOf(List p1, List p2, CrossDimerAnalysis cda) {
        List crossdimers=new ArrayList();
        
        for(int i=0; i < p1.size(); i++) {
            Primer p=(Primer)p1.get(i);
            for(int j=0; j < p2.size(); j++) {
                Primer q=(Primer)p2.get(j);
                crossdimers.addAll(SekStrukturFactory.getCrossdimer(p,q,cda));
                crossdimers.addAll(SekStrukturFactory.getCrossdimer(q,p,cda));
            }
        }
        return crossdimers;
    }
    private static boolean passtMitKompatiblenCD(List p1, List p2) {
        for(int i=0; i < p1.size(); i++) {
            Primer p=(Primer)p1.get(i);
            for(int j=0; j < p2.size(); j++) {
                Primer q=(Primer)p2.get(j);
                if(!p.passtMitKompCD(q))
                    return false;
            }
        }
        return true;
    }

    protected static class SBEPrimerProxy implements Multiplexable{

    	private List p1;
    	private List p2;
		private final Collection edgecol=new HashSet();
		private String einbau="";
		
		public SBEPrimerProxy(List p1, List p2, String einbau){
    		this.p1=p1;
    		this.p2=p2;
    		this.einbau=einbau;
    	}
		public String toString() {
      return "SBEPrimerproxy: "+p1+" and "+p2;      
        }
        public void setPlexID(String s) {
            for (Iterator it = p1.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                m.setPlexID(s);
            }
            for (Iterator it = p2.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                m.setPlexID(s);
            }
		}
		public String getName() {
            StringBuffer sb = new StringBuffer();
            for (Iterator it = p1.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                sb.append(m.getName()).append('+');
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append('_');
            for (Iterator it = p2.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                sb.append(m.getName()).append('+');
            }
            sb.deleteCharAt(sb.length()-1);
            return new String(sb);
		}
        public int realSize() {
            return 2;
        }
        public boolean passtMit(Multiplexable other) {
            edgecol.clear();
            List othermultis=getAllPrimers(other);
            for (Iterator it = othermultis.iterator(); it.hasNext();) {
                Primer primer = (Primer) it.next();
                if(!passenWirMit(primer)) {
//                    edgecol.clear();
//                    edgecol.add(new IncompCDEinbauEdge(this,other));
                    return false;
                }
            }
            return true;
        }
        /**
         * @param other
         * @param other
         * @return
         */
        private boolean passenWirMit(Primer other) {
            if(passenEinbautenMit(other)==false)
                return false;
            for (Iterator it = p1.iterator(); it.hasNext();) {
                Primer p = (Primer) it.next();
                if(!p.passtMit(other)){
                    edgecol.addAll(p.getLastEdges());
                    return false;
                }
            }
            for (Iterator it = p2.iterator(); it.hasNext();) {
                Primer p = (Primer) it.next();
                if(!p.passtMit(other)){
                    edgecol.addAll(p.getLastEdges());
                    return false;
                }
            }
            return true;
        }
        /**
         * @param other
         * @return
         */
        private boolean passenEinbautenMit(Primer p) {
            String snp=p.getSNP();
            for (int i = 0; i < einbau.length(); i++) {
                if(snp.indexOf(einbau.charAt(i))!=-1){//inkompatibel mit diesem Primer
                    return false;
                }
            }
            return true;
        }

        public List getIncludedElements() {
            List result = new ArrayList(p1.size()+p2.size());
            result.addAll(p1);
            result.addAll(p2);
            return result;
        }
        public Collection getLastEdges() {
            return new HashSet(edgecol);
        }
        public String getPlexID() {
            for (Iterator it = p1.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                return m.getPlexID();
            }
            for (Iterator it = p2.iterator(); it.hasNext();) {
                Multiplexable m = (Multiplexable) it.next();
                return m.getPlexID();
            }
            return null;
        }

    }
    public synchronized static void stop(boolean s) {
        stopped=s;
    }
    public synchronized static boolean isStopped() {
        return stopped;
    }
    private static class IncompCDEinbauEdge extends MyUndirectedEdge{

        public IncompCDEinbauEdge(Object sourceVertex, Object targetVertex) {
            super(sourceVertex, targetVertex);
        }
        public String toString() {
            return "comp._CD_nucleotide";
        }
        public String matchString() {
            return getSource().toString()+this.toString()+getTarget().toString();
        }
    }
}
