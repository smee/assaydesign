/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

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

    public int maxPlexSize() {
        return maxplex;
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
}