/*
 * Created on 12.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.sbe.calculators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Ungerichteter Graph, als Adjazenzliste gespeichert.
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AdjGraph{
	private Map knotenliste;
	private Map subgraphmap;
	
	public AdjGraph(){
		this.knotenliste=new HashMap();
		this.subgraphmap=new HashMap();
	}
	public AdjGraph(boolean[][] admatrix){
		this();
		for (int i = 0; i < admatrix.length; i++) {
			addKnoten(i);
		}
		for (int i = 0; i < admatrix.length; i++) {
			for (int j = 0; j < admatrix.length; j++) {
				if(admatrix[i][j])
					addKante(i,j);
			}
		}
	}
	/**
	 * Fügt neuen Knoten hinzu, wenn dieser noch nicht existieren sollte. Eigentlich überflüssig.
	 * @param id
	 */
	public void addKnoten(int id){
		Integer i=new Integer(id);
		if(!knotenliste.containsKey(i)){
			knotenliste.put(i,new ArrayList());
		}
	}
	/**
	 * Füge Kante hinzu. Da diese ungerichtet ist, wird sie in der Adjazenzliste beider Knoten gepeichert.
	 * @param k1
	 * @param k2
	 */
	public void addKante(int k1, int k2){
		Integer i1=new Integer(k1);
		Integer i2=new Integer(k2);
		Object o=knotenliste.get(i1);
		if(null == o){
			o=new ArrayList();
			knotenliste.put(i1,o);
		}
		List l=(List)o;
		if(!l.contains(i2)){
			l.add(i2);
		}
		o=knotenliste.get(i2);
		if(null == o){
			o=new ArrayList();
			knotenliste.put(i2,o);
		}
		l=(List)o;
		if(!l.contains(i1)){
			l.add(i1);
		}
	}
	public int knotenCount(){
		return knotenliste.size();
	}
	/**
	 * Zerlegt den Graphen in alle nicht zusammenhängenden Untergraphen.
	 *
	 */
	public void findSubGraphs(){
		Map orgmap=new HashMap(knotenliste);
		subgraphmap=new HashMap();
		int index=0;
		while(0 < orgmap.size()){
			List queue=new ArrayList();		//enthält alle noch zu verarbeitenden KNoten
			Integer i=(Integer)orgmap.keySet().iterator().next(); //ein beliebiger Knoten
			queue.add(i);
			List indiceslist=new ArrayList();
			while(!queue.isEmpty()){
				i=(Integer) queue.get(0);
				queue.remove(0);
				if(!indiceslist.contains(i))
					indiceslist.add(i);
				if(orgmap.containsKey(i)){
					List isucc=(List)orgmap.get(i);		//Liste aller Nachfolger von Knoten i
					orgmap.remove(i);
					for (Iterator it = isucc.iterator(); it.hasNext();) {
						Integer s = (Integer) it.next();
						if(!queue.contains(s))
							queue.add(s);
					}
				}
			}
			subgraphmap.put(new Integer(index),indiceslist);
			index++;
		}
	}
	public int subGraphCount(){
		return subgraphmap.size();
	}
	/**
	 * Liefert Liste Integer, in denen jeweils die Indizes der zugehörigen Knoten des Subgraphen stehen.
	 * @param i
	 * @return
	 */
	public List getSubGraphIndices(int i){
		if(0 > i || i>=subgraphmap.size())
			throw new IllegalArgumentException("No such subgraph: "+i);
		return (List)subgraphmap.get(new Integer(i));
	}
}