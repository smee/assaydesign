/*
 * Created on 08.11.2004
 *
 */
package biochemie.verw;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.alg.ConnectivityInspector;
import org._3pq.jgrapht.graph.SimpleGraph;
import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

import biochemie.util.GraphWriter;

import com.Ostermiller.util.ExcelCSVParser;

/**
 * @author Steffen Dienst
 *
 */
public class Verwandtschaft {
	public static int[] lookup;
	private static final int A = 0;
	private static final int B = 1;
	private static final int AB = 2;
	private static final int NONE = 3;

	static class Kind{
		private String name;
		int type;
		private int pos;

		public Kind(String name, int pos){
			this.name=name;
			this.pos =pos;
		}
		public void setType(int type){
			this.type=type;
		}
	/**
	 * Override hashCode.
	 *
	 * @return the Objects hashcode.
	 */
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
		hashCode = 31 * hashCode + pos;
		return hashCode;
	}
	/**
	 * Returns <code>true</code> if this <code>Kind</code> is the same as the o argument.
	 *
	 * @return <code>true</code> if this <code>Kind</code> is the same as the o argument.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		Kind castedObj = (Kind) o;
		return ((this.name == null ? castedObj.name == null : this.name
			.equals(castedObj.name)));
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Kind:");
		buffer.append(" name: ");
		buffer.append(name);
		buffer.append(" pos: ");
		buffer.append(pos);
		buffer.append("]");
		return buffer.toString();
	}
	}
	static class Eltern{
		private Kind mama;
		private Kind papa;


		public Eltern(Kind mama, Kind papa){
			this.mama=mama;
			this.papa=papa;
		}

		public boolean kannNichtVerwandtSein(Kind k){
			if(k.type == NONE || mama.type == NONE || papa.type == NONE)
				return false;
			if(k.type == A){
				if(mama.type == B || papa.type == B)
					return true;
				return false;
			}else if(k.type == B){
				if(mama.type == A || papa.type == A)
					return true;
				return false;
			}else{//k.type == AB
				if(mama.type == papa.type && mama.type != AB)
					return true;
				return false;
			}
		}


	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Eltern:");
		buffer.append(" mama: ");
		buffer.append(mama.name);
		buffer.append(" papa: ");
		buffer.append(papa.name);
		buffer.append("]");
		return buffer.toString();
	}
	/**
	 * Override hashCode.
	 *
	 * @return the Objects hashcode.
	 */
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + (mama == null ? 0 : mama.hashCode());
		hashCode = 31 * hashCode + (papa == null ? 0 : papa.hashCode());
		return hashCode;
	}
	/**
	 * Returns <code>true</code> if this <code>Eltern</code> is the same as the o argument.
	 *
	 * @return <code>true</code> if this <code>Eltern</code> is the same as the o argument.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		Eltern castedObj = (Eltern) o;
		return ((this.mama == null ? castedObj.mama == null : this.mama
			.equals(castedObj.mama)) && (this.papa == null
			? castedObj.papa == null
			: this.papa.equals(castedObj.papa)));
	}
	}

	static class VerwGraph{
		boolean[][] g;
		private Kind[] kinder;
		private Eltern[][] eltern;

		public VerwGraph(Kind[] kinder, Eltern[][] eltern){
			this.kinder=kinder;
			this.eltern=eltern;
			g=new boolean[kinder.length][(eltern.length + 1) * eltern.length / 2 - eltern.length +1];
			System.out.println("Using field of dimensions "+g.length+"x"+g[0].length);
		}
		public void addEdge(int row, int column){
			g[row][column]=true;
		}
		public UndirectedGraph getReverseGraph(){
			UndirectedGraph grev=new SimpleGraph();
			grev.addAllVertices(java.util.Arrays.asList(kinder));
			System.out.println("Adding vertices to reverse graph...");
			for (int i = 0; i < eltern.length; i++) {
				for (int j = i + 1; j < eltern[i].length; j++) {
					grev.addVertex(eltern[i][j]);
				}
			}
			System.out.println("Adding arcs to reverse graph...");
			for (int a = 0; a < eltern.length; a++) {
				System.out.print('.');
				for (int b = a + 1; b < eltern[a].length; b++) {
					for (int c = 0; c < kinder.length; c++) {
						if(g[c][lookup[a] + (eltern.length - b)] == false){
							try {
								grev.addEdge(eltern[a][b],kinder[c]);
							} catch (RuntimeException e) {
								e.printStackTrace();
								System.out.println("contains kid: "+grev.containsVertex(kinder[c]));
								System.out.println("contains parent: "+grev.containsVertex(eltern[a][b]));
								System.exit(1);
							}
						}
					}
				}
			}
			return grev;
		}
	}
	private static void initLookup(int len){
		System.out.println("lookup len = "+len);
		lookup=new int[len];
		int sum=0;
		int inc=len - 1;
		for (int i = 0; i < lookup.length; i++) {
			lookup[i]=sum;
			sum+=inc;
			inc--;
		}
		System.out.println(biochemie.util.Helper.toString(lookup));
	}
	public static void main(String[] args) {
		try {
			ExcelCSVParser p=new ExcelCSVParser(new FileReader(args[0]),';');

			System.out.println("Reading file "+args[0]);

			String[][] val=p.getAllValues();
			initLookup(val.length - 1);
			System.out.println((val.length-1)+"x"+(val[0].length-1));

			Kind[] kinder=new Kind[val.length-1];

			System.out.println("Adding children...");

			for (int i = 1; i < val.length; i++) {
				kinder[i-1]=new Kind(val[i][0], i);
			}
			Eltern[][] eltern=new Eltern[kinder.length][kinder.length];
			System.out.println("Adding parents...");
			for (int i = 0; i < kinder.length; i++) {
				for (int j = i + 1; j < kinder.length; j++) {
					eltern[i][j]=new Eltern(kinder[i], kinder[j]);
				}
			}
			VerwGraph g=new VerwGraph(kinder,eltern);

			System.out.println(eltern.length+" possible parents...");
			//setze kanten bei allen nicht erwuenschten elternpaaren
			for (int a = 0; a < eltern.length; a++) {
				for (int b = a + 1; b < eltern.length; b++) {
//					if(b != a + 2) //2er
//					for (int c = 0; c < kinder.length; c++) {
//						g.addEdge(c,lookup[a] + (eltern.length - b));
//					}
					//System.out.println(a+" "+b);
					try {
						g.addEdge(a,lookup[a]+(eltern.length - (b)));
						g.addEdge(b,lookup[a]+(eltern.length - (b)));
					} catch (RuntimeException e1) {
						e1.printStackTrace();
						System.out.println("a="+a+", b="+b+" lookup[a]="+lookup[a]+", (lookup[a]+(eltern.length - (b))="+(lookup[a]+(eltern.length - (b))));
						System.exit(1);
					}
				}
//					g.addEdge(a,lookup[a] + (eltern.length - (a+2)));  //2er
//					g.addEdge(a+2,lookup[a] + (eltern.length - (a+2)));		//2er
			}

			for (int i = 1; i < val[0].length; i++) {
//				if(i == 30)
//					break;
				System.out.println("Evaluating column "+i+" of "+val[0].length+"("+val[0][i]+")...");
				//determine the different value types
				String stringA="", stringB="";
				for (int j = 1; j < val.length; j++) {
					String entry=val[j][i];
					if(entry.indexOf('/')!=-1){
						int pos=val[j][i].indexOf('/');
						stringA=entry.substring(0,pos).trim();
						stringB=entry.substring(pos+1).trim();
						break;
					}
				}
				if(stringA.length()==0 && stringB.length()==0){
					System.out.println("Couldn't find values in column "+val[0][i]);
					continue;
				}


				for (int j = 1; j < val.length; j++) {
					String entry=val[j][i];
					if(entry.indexOf('/')!=-1){
						kinder[j-1].setType(AB);
					}else if(entry.indexOf(stringA) != -1){
						kinder[j-1].setType(A);
					}else if(entry.indexOf(stringB) != -1){
						kinder[j-1].setType(B);
					}else{
						kinder[j-1].setType(NONE);

					}
				}


				for (int a = 0; a < eltern.length; a++) {
					for (int b = a + 1; b < eltern.length; b++) {
						for (int c = 0; c < kinder.length; c++) {
							if( eltern[a][b].kannNichtVerwandtSein(kinder[c])){
								try {
									g.addEdge(c,lookup[a] + (eltern.length - b));
								} catch (RuntimeException e1) {
									e1.printStackTrace();
									System.out.println("\na: "+a);
									System.out.println("b: "+b);
									System.out.println("lookup[a]: "+lookup[a]);
									System.out.println("lookup[a] + (eltern.length - b): "+(lookup[a] + (eltern.length - b)));
									System.exit(1);
								}
							}
						}
					}
				}
			}

			System.out.println("Creating reverse graph...");

			UndirectedGraph grev=g.getReverseGraph();


			System.out.println("Searching connected sets...");

			ConnectivityInspector ci=new ConnectivityInspector(grev);
			System.out.println(ci.connectedSets().size()+" connected sets!");
			List sets=ci.connectedSets();
			Algorithms.remove(sets.iterator(), new UnaryPredicate(){
				public boolean test(Object obj) {
					return ((Set)obj).size()<=1;
				}
			});

			Map uniq=new HashMap();
			for (Iterator it = sets.iterator(); it.hasNext();) {
				Set family = (Set) it.next();
				for (Iterator it2 = family.iterator(); it2.hasNext();) {
					Object o=it2.next();
					if(o instanceof Kind){
						Kind k=(Kind)o;
						Integer i=(Integer) uniq.get(k.name);
						if(i==null)
							i=new Integer(0);
						uniq.put(k.name,new Integer(i.intValue()+1));
					}else{
						Eltern e=(Eltern)o;
						Integer i=(Integer) uniq.get(e.mama.name);
						if(i==null)
							i=new Integer(0);
						uniq.put(e.mama.name,new Integer(i.intValue()+1));
						i=(Integer) uniq.get(e.papa.name);
						if(i==null)
							i=new Integer(0);
						uniq.put(e.papa.name,new Integer(i.intValue()+1));
					}
				}
			}
			Set uniqnames=new TreeSet();
			for (Iterator it = uniq.keySet().iterator(); it.hasNext();) {
				String name=(String) it.next();
				int i=((Integer)uniq.get(name)).intValue();
				if(i == 1)
					uniqnames.add(name);
			}
			System.out.println("Found "+uniqnames.size()+" unique names:");
			System.out.println(uniqnames);

			System.out.println("-----------------------------------");
			Set notinfamilies=new TreeSet();
			for (int i = 0; i < kinder.length; i++) {
				notinfamilies.add(kinder[i].name);
			}
			notinfamilies.removeAll(uniqnames);

			for (Iterator it = uniqnames.iterator(); it.hasNext();) {
				String name = (String) it.next();
				assert !notinfamilies.contains(name);
			}
			System.out.println("Nicht zuordenbare Familienmitglieder ("+notinfamilies.size()+") :");
			System.out.println(notinfamilies);
			System.out.println("-----------------------------------");

			System.out.println("\nEindeutige Familien, weil min. ein Familienmitglied nur genau einmal auftaucht:");

			int counter = 1;
			for (Iterator it = sets.iterator(); it.hasNext();) {
				Set family = (Set) it.next();
				if(family.size() == 2){
					for(Iterator famit=family.iterator();famit.hasNext();){
						Object o= famit.next();
						if(o instanceof Kind){
							if(uniqnames.contains(((Kind)o).name)){
								System.out.println(counter+": "+family);
								counter++;
								break;
							}
						}else{
							if(uniqnames.contains(((Eltern)o).mama.name)){
								System.out.println(counter+": "+family);
								counter++;
								break;
							}else if(uniqnames.contains(((Eltern)o).papa.name)){
								System.out.println(counter+": "+family);
								counter++;
								break;
							}
						}
					}
				}
			}
			System.out.println(sets.size()+" possible families found:");
			for (Iterator it = sets.iterator(); it.hasNext();) {
				Set family = (Set) it.next();
				System.out.println(family);
				List l=new ArrayList(family);

				List names=(List) Algorithms.collect(Algorithms.apply(l.iterator(),new UnaryFunction(){
					public Object evaluate(Object obj) {
						return obj.toString().replace(' ','_');
					}
				}));
				GraphWriter gw=new GraphWriter(names,"family",GraphWriter.TGF);
				for (int i = 0; i < l.size(); i++) {
					for (int j = 0; j < l.size(); j++) {
						if(grev.containsEdge(l.get(i),l.get(j)))
							gw.addArc(i,j,"");
					}
				}
				gw.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
