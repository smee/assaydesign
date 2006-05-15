/*
 * Created on 23.11.2004
 *
 */
package biochemie.sbe.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import biochemie.sbe.CleavablePrimerFactory;
import biochemie.sbe.PrimerFactory;
import biochemie.sbe.SBEOptions;
import biochemie.sbe.WrongValueException;
import biochemie.sbe.multiplex.MultiplexableFactory;
import biochemie.util.Helper;

/**
 * @author Steffen Dienst
 *
 */
public class SBEPrimerReader {
	protected List list;
    protected List sbec;
    protected SBEOptions cfg;

    public SBEPrimerReader() {

    }
    /**
     * @param filename
     * @param cfg
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void initForRead(String filename, SBEOptions cfg) throws FileNotFoundException, IOException {
        this.cfg = cfg;
    	BufferedReader br= new BufferedReader(new FileReader(new File(filename)));
        list = new LinkedList();

        String line=br.readLine(); //skip header

        while (null != (line = br.readLine())) {
            if (!SBEConfig.isEmptyRow(line))
                list.add(line);
        }
    }
    private CleavablePrimerFactory useRow(int n) throws WrongValueException  {
        if(n>=getCount() || 0 > n)
            return null;
        String line=Helper.clearEmptyCSVEntries((String)list.get(n));
        return CleavablePrimerFactory.getSBECandidateFromInputline(cfg,line);
    }
    /**
     * Erzeuge Liste mit Kandidaten. Funktioniert nur einmal, da die originale Liste intern gehalten wird, damit beim Speichern spaeter
     * keine verloren gehen.
     * @throws WrongValueException
     * @see CleavablePrimerFactory
     */
    public List getSBECandidates(String filename, SBEOptions cfg) throws IOException, WrongValueException {
        initForRead(filename, cfg);
        if(null == sbec) {
            sbec=new ArrayList();
            for(int i=0;i<getCount();i++) {
                CleavablePrimerFactory sbec=useRow(i);
                this.sbec.add(sbec);
            }
        }
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            CleavablePrimerFactory s = (CleavablePrimerFactory) it.next();
            if(0 != s.getGivenMultiplexID().length() && !s.hasPL()) {
                throw new IllegalArgumentException("Primer ID="+s.getId()+" has no given cleavable linker, so it can't be in the given multiplex "+s.getGivenMultiplexID()+'!');
            }
        }
        return collapseMultiplexes(sbec,cfg);
    }
    /**
	 * @param sbec
     * @return
	 */
	public static List collapseMultiplexes(List sbec, SBEOptions cfg) {
		Collections.sort(sbec,new Comparator() {
            public int compare(Object arg0, Object arg1) {
                return ((PrimerFactory)arg0).getGivenMultiplexID().compareTo(((PrimerFactory)arg1).getGivenMultiplexID());
            }
        });
        List l=new ArrayList();
        if(0 == sbec.size())
            return l;
        int startpos=0;
        for (int i=0; i < sbec.size();i++) {
            PrimerFactory s = (PrimerFactory) sbec.get(i);
            String id=s.getGivenMultiplexID();
            if(i+1 == sbec.size() || 0 == id.length() || !(((PrimerFactory) sbec.get(i+1)).getGivenMultiplexID()).equals(id)) {
                List knoten=new LinkedList();
                for(int idx=startpos; idx <= i; idx++)
                    knoten.add(sbec.get(idx));
                if(1 == knoten.size())
                    l.add(sbec.get(startpos));
                else
                    l.add(new MultiKnoten(knoten,id));
                startpos=i+1;
            }
        }
        return l;
	}
	/**
     * Gibt die Anzahl Zeilen der Anfrage zurueck.
     * @return
     */
    private int getCount() {
        return list.size();
    }
    /**
     * @param outname
     */
    public void writeSBEResults(String outname) {
        writeSBEResults(outname,sbec);
    }

    public static void writeSBEResults(String filename, List sbec) {
        if(sbec.size()==0)
            return;
        String[] header=((PrimerFactory)sbec.get(0)).getCsvheader().split(";");
        StringBuffer sb=new StringBuffer(StringUtils.join(header,';') +"\n");

        for (int i= 0; i < sbec.size(); i++) {
            String line=((MultiplexableFactory) sbec.get(i)).getCSVRow();
            sb.append(line);
            sb.append('\n');
        }
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(filename));
            bw.write(sb.toString());
            bw.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben von "+filename+". Fehler: "+e.getMessage());
        }
    }

}
