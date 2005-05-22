/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

import java.util.ArrayList;
import java.util.List;

import biochemie.sbe.multiplex.Multiplexable;


class PCRPair implements Multiplexable{
    PCRPrimer leftp, rightp;
    int maxplex;
    private String edgereason;
    PCRPair(PCRPrimer leftp, PCRPrimer rightp, int maxplex){
        this.leftp=leftp;
        this.rightp=rightp;
        this.maxplex=maxplex;
        edgereason="";
    }
    public void setPlexID(String s) {
        leftp.setPlexID(s);
        rightp.setPlexID(s);
    }

    public String getName() {
        return leftp.getId()+","+leftp.getPos();
    }

    public boolean passtMit(Multiplexable other) {
        if(other instanceof PCRPrimer){
            boolean b=leftp.passtMit(other);
            if(!b){
                edgereason=leftp.getEdgeReason();
                return false;
            }
            b=rightp.passtMit(other);
            if(!b){
                edgereason=rightp.getEdgeReason();
                return false;
            }

        }
        PCRPair p=(PCRPair)other;
        boolean b= leftp.passtMit(p.leftp)
        && leftp.passtMit(p.rightp);
        if(!b){
            edgereason=leftp.getEdgeReason();
            return false;
        }
        b= rightp.passtMit(p.leftp)
        && rightp.passtMit(p.rightp);
        if(!b){
            edgereason=rightp.getEdgeReason();
            return false;
        }
        return true;
    }

    public String toString(){
        return getName();
    }
    /**
     * @return
     */
    public String getEdgeReason() {
        return edgereason;
    }
    public String getCSVLine() {
        return leftp.getInputLine();
    }
    public int realSize() {
        return 1;
    }
    public List getIncludedElements() {
        List result= new ArrayList(2);
        result.add(leftp);
        result.add(rightp);
        return result;
    }
}