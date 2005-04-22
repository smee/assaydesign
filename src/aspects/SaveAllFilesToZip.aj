/*
 * Created on 05.06.2004
 *
 */
package aspects;
//import org.jgap.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import biochemie.sbe.io.SBEPrimerReader;
import biochemie.util.*;
import biochemie.util.config.GeneralConfig;
import java.io.*;
import java.util.zip.*;
import java.util.Set;
import java.util.HashSet;
/**
 *
 * @author Steffen
 *
 */
public aspect  SaveAllFilesToZip {
	BufferedWriter bw;
	String configname;
	
	long seed;
	Set usedFiles=new HashSet();

	/**
	 * Irgendetwas im Paket biochemie..* verwendet Dateien.
	 */
	pointcut fileUsed(String filename): (call ( FileReader.new(String))
										|| call( FileWriter.new(String))
										|| call( FileInputStream.new(String))
										|| call( public static void LogStdStreams.initializeErrorLogging(String,*))
										|| call( FileOutputStream.new(String,*))
										|| call( FileOutputStream.new(String))
										|| call( File.new(String))
										|| call( PrintStream+.new(String,*))
										|| call( PrintStream+.new(String)))
										&& args(filename)
										&& (within(biochemie..*)
										        || within(GeneralConfig+)
										        || within(biochemie.util..*));
						
	pointcut resultsWritten(): (call( void SBEPrimerReader.write*(..)) || call( void startAnalysis())
										|| execution (void exitApp()));
	
	
	
	before(String filename):fileUsed(filename){
	    addFile(filename);
	}


	after(): resultsWritten(){
	    addFile("minisbe.log");
	    addFile("calcdalton.log");
	    addFile("pcr.log");
	    for (java.util.Iterator it=usedFiles.iterator();it.hasNext();) {
	        String file=(String)it.next();
            if(file==null || file.length()==0 || !(new File(file)).exists()) {
                it.remove();
            }
            
        }
	    String[] f=(String[])usedFiles.toArray(new String[usedFiles.size()]);
	    createZipFile(f);
	}
    
    private void addFile(String filename) {
        try {
            filename = new File(filename).getCanonicalPath();
        }catch(IOException e) {}
        usedFiles.add(filename);
        System.out.println("used file: "+filename);
    }
    
	private void createZipFile(String[] files){
	    try {
	        String zipfilename=Helper.dateFunc()+".zip";
	        final int BUFFER = 2048;
	        
	        BufferedInputStream origin = null;
	        FileOutputStream dest = new 
	        FileOutputStream(zipfilename);
	        ZipOutputStream out = new ZipOutputStream(new 
	                BufferedOutputStream(dest));
	        out.setMethod(ZipOutputStream.DEFLATED);
	        byte data[] = new byte[BUFFER];
	        for (int i=0; i<files.length; i++) {
	            System.out.println("Adding: "+files[i]);
	            FileInputStream fi = new FileInputStream(files[i]);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = zipFunc(files[i]);
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, 
	                    BUFFER)) != -1) {
	                out.write(data, 0, count);
	            }
	            origin.close();	            
	        }
	        out.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
private ZipEntry zipFunc(String filePath) throws IOException
  {
    File ffilePath = new File(filePath);
    String path = "";
        File f = new File("");
        String pathToHere = f.getAbsolutePath();
        path = ffilePath.getAbsolutePath();
        path = path.substring(path.indexOf(pathToHere + File.separator)
            + pathToHere.length());
    return new ZipEntry(path);
  }
}
