/*
 * Created on 06.11.2004
 *
 */
package biochemie.pcr.matcher;

import java.util.ArrayList;
import java.util.List;

import org._3pq.jgrapht.Edge;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import biochemie.sbe.multiplex.Multiplexable;
import biochemie.util.config.GeneralConfig;
import biochemie.util.edges.MyUndirectedEdge;


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
    public void setNewConfig(GeneralConfig cfg) {
        leftp.setNewConfig(cfg);
        if(rightp != null)
            rightp.setNewConfig(cfg);
    }
    /*
     * TODO die kanten haben pcrprimer als knoten, sollten aber besser pcrpairs sein
     *  (non-Javadoc)
     * @see biochemie.sbe.multiplex.Multiplexable#passtMit(biochemie.sbe.multiplex.Multiplexable)
     */
    public boolean passtMit(Multiplexable other) {
        if(other instanceof PCRPrimer){
            boolean b=leftp.passtMit(other);
            if(!b){
                edge=new MyDefaultEdge(this,other);
                return false;
            }
            b=rightp.passtMit(other);
            if(!b){
                edge=new MyDefaultEdge(this,other);
                return false;
            }
            return true;
        }else
            if(other instanceof PCRPair){
                PCRPair p=(PCRPair)other;
                boolean b= leftp.passtMit(p.leftp)
                && leftp.passtMit(p.rightp);
                if(!b){
                    edge=new MyDefaultEdge(this,other);
                    return false;
                }
                b= rightp.passtMit(p.leftp)
                && rightp.passtMit(p.rightp);
                if(!b){
                    edge=new MyDefaultEdge(this,other);
                    return false;
                }
                return true;
            }else
                return other.passtMit(this);//kenn ich nicht
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
    public boolean equals(Object other) {
        if(other instanceof PCRPair) {
            PCRPair o=(PCRPair)other;
            return leftp.equals(o.leftp) && rightp.equals(o.rightp);
        }
        return false;
    }
    public int hashCode() {
        return new HashCodeBuilder(33,451).append(leftp).append(rightp).toHashCode();
    }
}