/*
 * Created on 25.11.2004
 *
 */
package biochemie.pcr.modules;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import sun.net.www.protocol.http.HttpURLConnection;
import biochemie.pcr.PCR;
import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.modules.BLAT.BlatResultEntry;
import biochemie.util.Helper;
import biochemie.util.INetError;


public class InetSource implements BlatSource{
    private final String GENOMEASSEMBLY;
	private String cookie= "";
    private final int ORIGINALHASH;
    public static final String blaturl= "http://genome.ucsc.edu/cgi-bin/hgBlat";
    //static public final String blaturl="http://genome.cribi.unipd.it/cgi-bin/hgBlat";
    /**
     * Hashcode der aktuellen Seite OHNE die hgsid!
     */
    int blathash;
	private String hgsid;
	private final PCRConfig config;

	/**
	 * @param cfg
	 * @throws BlatException
	 */
	public InetSource(PCRConfig cfg) throws BlatException {
    	this.config = cfg;
		ORIGINALHASH= config.getInteger("BLAT_HASHCODE",-1);
    	GENOMEASSEMBLY = config.getString("ASSEMBLY","hg16");
        initProperties();
	}
	private void initProperties() throws BlatException{
        //setze Proxy-Server, wenn nötig
        if(config.getBoolean("USEPROXY",false)){
            String host=config.getString("PROXYHOST");
            String port=config.getString("PROXYPORT");
            if(PCR.debug)
                System.out.println("using proxy: "+host+", port: "+port);
            System.getProperties().put( "proxySet", "true" );
            System.getProperties().put( "proxyHost",host);
            System.getProperties().put( "proxyPort", port);
        }
		blathash= getAndHashAktuelleBLATPage();
        if (PCR.debug) {
            System.out.println("Aktueller Hashcode von BLAT : " + blathash);
        }
        if (blathash != ORIGINALHASH) {
            throw new BlatException("Wrong hashcode. The website "+blaturl+" seems to have changed!");
        }
	}
	   /**
     * Test, ob sich die BLATPage geändert hat. Momentan gilt der Hash nur für die .edu-Site.
     * Als Seiteneffekt wird die VAriable hgsid gesetzt, eine Art privates Cookie der Seite.
     * @return
     */
    private int getAndHashAktuelleBLATPage() throws BlatException {
		BufferedReader in=null;
		try {
            URL url= new URL(blaturl + "?command=start");
            URLConnection urlcon= url.openConnection();
            in= new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
            StringBuffer sb= new StringBuffer();
            String s;
            while (null != (s = in.readLine())) {
                sb.append(s);
            }
            System.out.println(sb);
            this.cookie= urlcon.getHeaderField("Set-Cookie");
            if (null != cookie && -1 != cookie.indexOf(';')) {
                cookie= cookie.substring(0, cookie.indexOf(';'));
            }
            if (Helper.isJava14()) {
                this.hgsid= sb.substring(sb.indexOf("hgsid=") + 6);
                this.hgsid= hgsid.substring(0, hgsid.indexOf('\"'));
                return sb.toString().replaceAll(this.hgsid, "").hashCode();
            } else {
                String sbString= sb.toString();
                this.hgsid= sb.substring(sbString.indexOf("hgsid=") + 6);
                this.hgsid= hgsid.substring(0, hgsid.indexOf('\"'));
                int start= 0;
                while (-1 != (start = sbString.indexOf("hgsid="))) {
                    sb.replace(start, start + hgsid.length(), "");
                }
                return sb.toString().hashCode();
            }
        } catch (MalformedURLException e) {
            throw new BlatException(e);
        } catch (IOException e) {
            throw new BlatException(e);
        }
        finally{
        	try {
        		if(null != in)
					in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
    }
    /**
     * K&uuml;mmert sich darum, die Ergebnisseite der BLAT-Analyse zu holen.
     * @param pcrproduct alles, was als input ans script geschickt wird. Entweder eine
     * einzelne sequenz oder mit ">eindeutige ID\n"sequenz1 ...
     * @return Html-Page von BLAT
     */
    private String getBLATAnalysisFromINet(String pcrproduct) throws INetError {
        if (PCR.debug) {
            System.out.println("Speichere BLAT-Anfrage nach blatanfrage_"+Thread.currentThread().getName()+".txt...");
            try {
                FileWriter fw= new FileWriter("blatanfrage_"+Thread.currentThread().getName()+".txt",false);
                fw.write("--------------------------------\n");
                fw.write(pcrproduct);
                fw.write("\n");
                fw.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String boundary= "--5f2tvOMbtHiVGgYuoWOeTb";
        try {
            URL url= new URL(blaturl);
            HttpURLConnection urlcon= (HttpURLConnection)url.openConnection();
            urlcon.setDoInput(true);
            urlcon.setDoOutput(true);
            urlcon.setUseCaches(false);
            ByteArrayOutputStream bout= new ByteArrayOutputStream(512);
            PrintWriter out= new PrintWriter(bout, true);
            //alles nach out schreiben
            out.print(boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"hgsid\"\r\n");
            out.print("\r\n");
            out.print(hgsid);
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"org\"\r\n");
            out.print("\r\n");
            out.print("Human");
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"db\"\r\n");
            out.print("\r\n");
            out.print(GENOMEASSEMBLY);
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"type\"\r\n");
            out.print("\r\n");
            out.print("BLAT's guess");
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"sort\"\r\n");
            out.print("\r\n");
            out.print("query,score");
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"output\"\r\n");
            out.print("\r\n");
            out.print("hyperlink");
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"userSeq\"\r\n");
            out.print("\r\n");
            out.print(pcrproduct);
            out.print("\r\n" + boundary + "\r\n");
            out.print("Content-Disposition: form-data; name=\"seqFile\"; filename=\"\"\r\n");
            out.print("\r\n");
            out.print("\r\n");
            out.print("\r\n" + boundary + "\r\n");
            out.println("Content-Disposition: form-data; name=\"Submit\"\r\n");
            out.print("\r\n");
            out.print("Submit\r\n");
            out.print("\r\n" + boundary + "--\r\n");
            //..................
            out.flush();
            urlcon.setRequestProperty("Content-Type", "multipart/form-data; boundary=5f2tvOMbtHiVGgYuoWOeTb");
            urlcon.setRequestProperty("Cookie", this.cookie);
            urlcon.setRequestProperty("Cookie2", "$Version=1");
            urlcon.setRequestProperty("Referer", "http://genome.ucsc.edu/cgi-bin/hgBlat?command=start");
            urlcon.setRequestProperty("User-Agent", "Opera/7.11 (Windows NT 5.1; U)  [de]");
            bout.writeTo(urlcon.getOutputStream());
            out.close();
            bout.close();
            String input;
            StringBuffer sb= new StringBuffer();
            BufferedReader in= new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
            while (null != (input = in.readLine())) {
                sb.append(input);
                sb.append('\n');
            }
            in.close();
            urlcon.disconnect();
            if (PCR.debug) {
                System.out.println("   Speichere BLAT-Antwort nach blatantwort_"+Thread.currentThread().getName()+".htm...");
                java.io.BufferedWriter bw= new java.io.BufferedWriter(new java.io.FileWriter("blatantwort.htm",false));
                bw.write("--------------------------\n");
                bw.write(sb.toString());
                bw.close();
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            throw new INetError(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new INetError(e);
        }
    }
	/**
     * Creates instanzes of private class BlatErg.
	 * @param res
	 * @param i
	 */
	public static List retrieveBlatergs(String res) {
		String[] lines=grepResultLinesFromBLATPage(res);
		List l=new ArrayList(lines.length);
		for (int i = 0; i < lines.length; i++) {
			StringTokenizer st = new StringTokenizer(lines[i]);
            st.nextToken(); //bezeichner
            st.nextToken(); //score
            int start = -1;
            int end = -1;
            try {
				start= Integer.parseInt(st.nextToken());
				end= Integer.parseInt(st.nextToken());
			} catch (NumberFormatException e) {
				System.err.println("Unknown position in blatresult!");
				continue;
			}
            st.nextToken(); //qsize
            st.nextToken(); //identity
            int chromosome = -1;
            try{
            	chromosome = Integer.parseInt(st.nextToken());
            }catch (NumberFormatException e) {}


			l.add(new BlatResultEntry(start, end,chromosome));
		}
		return l;
	}
    /**
     * Durchsucht BLAT-result nach allen Zeilen einer anfrage.
     * @param htmlpage BLAT-result
     * @param signatur eindeutige ID einer anfrage (bei einzelner Anfrage immer "YourSeq")
     * @return String[] mit Zeilen
     */
    public static String[] grepResultLinesFromBLATPage(String htmlpage) {
        Vector vtemp= new Vector();
        String tempstring;
        int start= 0;

        int end= 0;
        while (-1 != (start = htmlpage.indexOf("details</A> YourSeq" , start))) {
            start += 12;
            tempstring= htmlpage.substring(start);
            end= tempstring.indexOf("<A HREF");
            if (0 > end) {
                end= tempstring.indexOf("</TT>");
            }
            tempstring= tempstring.substring(0, end);
            vtemp.add(tempstring);
        }
        String[] result= new String[vtemp.size()];
        return (String[])vtemp.toArray(result);
    }
	public Collection getBlatResults(String pcrproduct) throws BlatException {
		try {
			String html= getBLATAnalysisFromINet(pcrproduct);
			return retrieveBlatergs(html);

		} catch (INetError e) {
			throw new BlatException(e);
		}

	}

}