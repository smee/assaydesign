/*
 * Created on 05.11.2004 by Steffen Dienst
 *
 */
package biochemie.pcr.matcher;

import java.util.HashSet;
import java.util.Set;

import biochemie.domspec.Primer;
import biochemie.domspec.SekStrukturFactory;
import biochemie.pcr.modules.CrossDimerAnalysis;
import biochemie.sbe.calculators.Multiplexable;

/**
 * @author Steffen Dienst
 * 05.11.2004
 */
public class PCRPrimer extends Primer {
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    private double maxtm;
    private double maxgc;
    private CrossDimerAnalysis cda;
    private String type;
    private int pos, maxplex;
    
    public PCRPrimer(String id, int pos,String seq, String type,double maxtm, double maxgc, int maxplex, CrossDimerAnalysis cda) {
        super(id,seq);
        this.maxtm=maxtm;
        this.maxgc=maxgc;
        this.cda=cda;
        this.type=type;
        this.pos=pos;
        this.maxplex=maxplex;
    }
    public boolean passtMit(Multiplexable o) {
        if(!(o instanceof PCRPrimer)) {
            edgereason="no PCRPrimer";
            return false;
        }
        PCRPrimer other=(PCRPrimer) o;
        if(id.equals(other.id)) {
            edgereason = "same primerinputfile";
            return false;
        }
        if(Math.abs(temp-other.temp) > maxtm) {
            edgereason="TMdiff too high";
            return false;
        }
        if(Math.abs(gcgehalt-other.gcgehalt) > maxgc) {
            edgereason="GCdiff too high";
            return false;
        }
        Set cd=SekStrukturFactory.getCrossdimer(this, other, cda);
        if(cd.size() != 0) {
            edgereason="crossdimer";
            return false;
        }
        return true;
    }

    public int maxPlexSize() {
        return maxplex;
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
}
