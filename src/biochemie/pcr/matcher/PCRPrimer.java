/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.pcr.matcher;

import java.util.HashSet;
import java.util.Set;

import biochemie.domspec.Primer;
import biochemie.domspec.SBESekStruktur;
import biochemie.domspec.SekStrukturFactory;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.config.GeneralConfig;
import biochemie.util.edges.GCDiffEdge;
import biochemie.util.edges.IdendityEdge;
import biochemie.util.edges.SecStructureEdge;
import biochemie.util.edges.TMDiffEdge;

/**
 * @author Steffen Dienst
 * 05.11.2004
 */
public class PCRPrimer extends Primer {
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    private CrossDimerAnalysis cda;
    private final String type;
    private final int pos;
    private final String inputline;
    private GeneralConfig cfg;
    
    public PCRPrimer(String filename, int pos, String inputline, String seq, String type) {
        super(filename,seq);
        this.type=type;
        this.pos=pos;
        this.inputline=inputline;
    }
    public void setNewConfig(GeneralConfig cfg) {
        this.cfg=cfg;
        this.cda=new CrossDimerAnalysis(cfg.getString("PARAM_CROSS_WINDOW_SIZE","")
                ,cfg.getString("PARAM_CROSS_MIN_BINDING","")
                ,Boolean.toString(false));
    }
    public boolean passtMit(Multiplexable o) {
        PCRPrimer other=(PCRPrimer) o;
        double tmp;
        if(id.equals(other.id)) {
            edge = new IdendityEdge(this,o);
            return false;
        }
        if((tmp=Math.abs(temp-other.temp)) > cfg.getDouble("MAX_TM_DIFF",0)) {
            edge=new TMDiffEdge(this,other,tmp);
            return false;
        }
        if((tmp=Math.abs(gcgehalt-other.gcgehalt)) > cfg.getDouble("MAX_GC_DIFF",0)) {
            edge=new GCDiffEdge(this,other,tmp);
            return false;
        }
        Set cd=SekStrukturFactory.getCrossdimer(this, other, cda);
        if(cd.size() != 0) {
            edge=new SecStructureEdge(this,other,(SBESekStruktur) cd.iterator().next());
            return false;
        }
        return true;
    }

    public String getType() {
        return type;
    }
    /**
     * @return
     */
    public int getPos() {
        return pos;
    }
	/* (non-Javadoc)
	 * @see biochemie.domspec.Primer#getSecStrucs()
	 */
	public Set getSecStrucs() {
		return new HashSet();
	}
    public String getInputLine() {
        return inputline+";"+id;
    }
}
