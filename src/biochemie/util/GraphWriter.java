/*
 * Created on 11.08.2004 by Steffen
 *
 */
package biochemie.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class GraphWriter{
    public static final int TGF=0;
    public static final int GML=1;
    public static final int XWG=2;
	private BufferedWriter bw=null;
    private final int type;
    private static int counter=0;
    
/*	public GraphWriter(SBECandidate[] sbec, String filename, int type){
		
        this.type=type;
        filename=setFilename(filename);
        try {
			bw=new BufferedWriter(new FileWriter(filename));
            List l=new ArrayList(sbec.length);
            for (int i = 0; i < sbec.length; i++) {
                l.add(sbec[i].getId());
            }
			init(l,type);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	/**
     * @param sbec
     * @throws IOException
     */
    private void init(List names, int type) throws IOException {
        switch (type) {
            case TGF :
                for (int i = 0; i < names.size(); i++) {
                    bw.write(i+" "+names.get(i)+'\n');
                }
                bw.write("#\n");                
                break;
            case GML :
                bw.write("graph [\n");
                for (int i = 0; i < names.size(); i++) {
                    bw.write("node [\n");
                    bw.write("id "+i+"\nlabel \""+names.get(i)+"\"\n]\n");
                }
                break;
            case XWG:
                bw.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" + 
                        "<!DOCTYPE WilmaGraph SYSTEM \"WilmaGraph.dtd\">\n" + 
                        "<WilmaGraph>\n<Cluster> \n");
                for (int i = 0; i < names.size(); i++) {
                    bw.write("  <Node ID=\""+i+"\">\n");
                    bw.write("<ViewType Name=\"DefaultNodeView\"/>\n");
                    bw.write("<Property Key=\"Label\" Value=\"N"+names.get(i)+"\"/>");
                    bw.write("</Node>\n");
                }
                break;
            default :
                break;
        }
    }
    public GraphWriter(List nodelabels, String filename,int type){
		this.type=type;
        filename=setFilename(filename);
        try {
			bw=new BufferedWriter(new FileWriter(filename));
			init(nodelabels,type);
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
     * @param filename
     * @return
     */
    private String setFilename(String filename) {
        filename+= ++counter;
        switch (type) {
            case TGF :
                filename+=".tgf";
                break;
            case GML :
                filename+=".gml";
                break;
            case XWG :
                filename+=".xwg";
                break;
            default :
                break;
        }
        
        return filename;
    }
    public void addArc(int i,int j,String label){
		if(null != bw){
			try {
                switch (type) {
                    case TGF :
        				bw.write(i+" "+j+' '+label+'\n');
                        break;
                    case GML :
        				bw.write("edge [\nsource "+i+"\ntarget "+j+"\n]\n");                        
                        break;
                    case XWG:
                        bw.write("<Edge EndID=\"N"+j+" \" StartID=\"N"+i+"\">\n");
                        bw.write("<ViewType Name=\"Plain Edge\"/>\n");
                        bw.write("<Property Key=\"Label\" Value=\""+label+"\"/>");
                        bw.write("</Edge>");
                        break;
                    default :
                        break;
                }
			} catch (IOException e) {}
		}
	}
	public void close(){
		if(null != bw){
			try {
                switch (type) {
                    case TGF :
        				bw.write("\n");                        
                        break;
                    case GML:
                        bw.write("]\n");
                        break;
                    case XWG:
                        bw.write("</Cluster>\n" + 
                                "</WilmaGraph>\n");
                        break;
                    default :
                        break;
                }
				bw.close();
			} catch (IOException e) {}
		}
	}
}