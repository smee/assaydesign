/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.sbe.multiplex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import biochemie.domspec.SBEPrimer;
import biochemie.domspec.SBESekStruktur;
import biochemie.domspec.SekStrukturFactory;
import biochemie.sbe.MiniSBE;
import biochemie.sbe.SBEOptionsProvider;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class Multiplexer {
	private static int plexnr=1;
    protected final boolean debug;
    protected SBEOptionsProvider cfg;

	public Multiplexer( SBEOptionsProvider cfg){
		this.cfg = cfg;
        this.debug=cfg.isDebug();
	}
	/**
     * Methode, die einer Menge von Multiplexables Multiplex-IDs zuweist.
     * Erwartet eine Liste von @link SBECandidates
	 * @param sbec
	 */
	public abstract void findMultiplexes(List sbec);

    /**
     * Liefert einen String, der als ID eines einzelnen Multiplexes dient.
     * @return
     */
    protected String getNextMultiplexID() {
        return 'M' + MiniSBE.getDatum() + '-' + plexnr++;
    }

    /**
     * @param sbec
     * @return
     */
    private List getNames(List structs) {
        List names=new ArrayList();
        for (int i = 0; i < structs.size(); i++) {
            Multiplexable s1=(Multiplexable) structs.get(i);
            names.add(s1.getName());
        }
        return names;
    }
    /**
     * Tags the multiplexables with an unique identifier.
     * @param maxclique
     */
    protected void giveMultiplexIDTo(Set maxclique) {
        String plexid=getNextMultiplexID();
        if(debug) System.out.println("New multiplex "+plexid+" for :");
        for (Iterator iter = maxclique.iterator(); iter.hasNext();) {
            Multiplexable struc = (Multiplexable) iter.next();
            if(debug) System.out.println(struc);
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
    protected List getEnhancedPrimerList(List sbep,SBEOptionsProvider cfg){
        //TODO verwenden beim Multiplexen
    	if(cfg.getAllCrossdimersAreEvil()==true)
    		return sbep;
    	List result=new ArrayList(sbep);
    	SBEPrimer[] primer=(SBEPrimer[]) sbep.toArray(new SBEPrimer[sbep.size()]);
    	for (int i = 0; i < primer.length; i++) {
			for (int j =i+1; j < primer.length; j++) {
				if(!primer[i].passtMit(primer[j]))
					continue;
				Set sekstruks=SekStrukturFactory.getCrossdimer(primer[i],primer[j],cfg);
				sekstruks.addAll(SekStrukturFactory.getCrossdimer(primer[j],primer[i],cfg));
				Set kompchars=new HashSet();
				for (Iterator it = sekstruks.iterator(); it.hasNext();) {
					SBESekStruktur cd = (SBESekStruktur) it.next();
					if(!cd.isIncompatible())
						kompchars.add(new Character(cd.bautEin()));
				}
				if(kompchars.size()!=0){
					String bautein="";
					for (Iterator it = kompchars.iterator(); it.hasNext();) {
						Character c = (Character) it.next();
						bautein+=c.charValue();
					}
					result.add(new SBEPrimerProxy(primer[i], primer[j],bautein));
				}
			}
		}
    	
    	
    	return result;
    }
    protected class SBEPrimerProxy implements Multiplexable{

    	private SBEPrimer p1;
    	private SBEPrimer p2;
		private String edgeReason="";
		private String einbau="";
		
		public SBEPrimerProxy(SBEPrimer p1, SBEPrimer p2, String einbau){
    		this.p1=p1;
    		this.p2=p2;
    		this.einbau=einbau;
    	}
		public void setPlexID(String s) {
			p1.setPlexID(s);
			p2.setPlexID(s);
		}
		public String getName() {
			return p1.getName()+"_"+p2.getName();
		}
		public boolean passtMit(Multiplexable other) {
			assert other instanceof SBEPrimer;
			SBEPrimer o = (SBEPrimer)other;
			
			String snp=o.getSNP();
			for (int i = 0; i < einbau.length(); i++) {
				if(snp.indexOf(einbau.charAt(i))!=-1){//inkompatibel mit diesem Primer
					edgeReason="imcomp._CD_nucleotide";
					return false;
				}
			}
			if(!p1.passtMit(other)){
				edgeReason=p1.getEdgeReason();
				return false;
			}else{
				boolean result=p2.passtMit(other);
				edgeReason=p2.getEdgeReason();
				return result;
			}
		}
		public int maxPlexSize() {
			return Math.min(p1.maxPlexSize(), p2.maxPlexSize()) - 1;
		}
		public String getEdgeReason() {
			return edgeReason;
		}
    }
}
