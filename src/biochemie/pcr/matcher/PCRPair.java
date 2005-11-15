/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

import java.util.ArrayList;
import java.util.List;

import org._3pq.jgrapht.Edge;

import biochemie.sbe.multiplex.Multiplexable;


class PCRPair implements Multiplexable{
    PCRPrimer leftp, rightp;
    int maxplex;
    private Edge edge;
    PCRPair(PCRPrimer leftp, PCRPrimer rightp, int maxplex){
        this.leftp=leftp;
        this.rightp=rightp;
        this.maxplex=maxplex;
        edge=null;
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
                edge=leftp.getLastEdge();
                return false;
            }
            b=rightp.passtMit(other);
            if(!b){
                edge=rightp.getLastEdge();
                return false;
            }

        }
        PCRPair p=(PCRPair)other;
        boolean b= leftp.passtMit(p.leftp)
        && leftp.passtMit(p.rightp);
        if(!b){
            edge=leftp.getLastEdge();
            return false;
        }
        b= rightp.passtMit(p.leftp)
        && rightp.passtMit(p.rightp);
        if(!b){
            edge=rightp.getLastEdge();
            return false;
        }
        return true;
    }

    public String toString(){
        return getName();
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
    public Edge getLastEdge() {
        return edge;
    }
}