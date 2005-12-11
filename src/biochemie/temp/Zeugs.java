package biochemie.temp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import biochemie.util.Helper;

public class Zeugs {

    /**
     * @param args
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void main2(String[] args) throws IOException, InterruptedException {
        File dir=new File(".");
        File[] files=dir.listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            String file = files[i].getAbsolutePath();
            String sorted=file+".sorted";
            sortFile(file,sorted);
            filterInvalidLines(sorted,file+".filtered");            
        }
    }
public static void main(String[] args) throws IOException {
    File dir=new File(".");
    File[] files=dir.listFiles(new FilenameFilter(){
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".filtered");
        }
    });
    Arrays.sort(files);
    for (int i = 0; i < files.length; i++) {
        String file = files[i].getAbsolutePath();
        int numpairs=findPairsIn(file);
        System.out.println("File \""+file+"\": "+numpairs+" pairs found.");
    }
    
}
    /**
 * @param file
 * @return
     * @throws IOException 
 */
private static int findPairsIn(String file) throws IOException {
    int num=0;
    BufferedReader br=new BufferedReader(new FileReader(file));
    String lastline=br.readLine();
    String line=null;
    while ((line=br.readLine())!=null) {
        if(isPair(lastline,line))
            num++;
        lastline=line;
    }
    return num;
}
    /**
     * @param lastline
     * @param line
     * @return
     */
    private static boolean isPair(String line1, String line2) {
        String[] tok1=line1.split("\t");
        String[] tok2=line2.split("\t");
        try{
        return tok1[6].equals(tok2[6]) && Math.abs(Integer.parseInt(tok1[11])-Integer.parseInt(tok2[11]))==1;
        }catch (NumberFormatException ne) {
            // TODO: handle exception
            return false;
        }
    }
    /**
     * @param absoluteFile
     * @throws IOException 
     * @throws InterruptedException 
     */
    private static void sortFile(String filename, String newfilename) throws IOException, InterruptedException {
        System.out.println("sort: "+filename);
        Process p=Runtime.getRuntime().exec("c:\\util\\usr\\local\\wbin\\sort.exe +11 -T c:/temp "+filename+" -o "+newfilename);
        int code=p.waitFor();
        System.out.println("sortprocess exited with return code: "+code);
        if(code!=0)
            System.exit(1);
    }

    /**
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void filterInvalidLines(String file, String newfile) throws FileNotFoundException, IOException {
        BufferedReader br=new BufferedReader(new FileReader(file));
        BufferedWriter bw=new BufferedWriter(new FileWriter(newfile));
        String line=null;
        int cnt1=0,cnt2=0;
        while ((line=br.readLine())!=null) {
            cnt1++;
            if(lineFitsDemands(line)){
                bw.write(line);
                bw.write("\n");
                cnt2++;
            }
        }
        br.close();
        bw.close();
        System.out.println("lines: "+cnt1);
        System.out.println("lines left: "+cnt2);
    }

    /**
     * @param line
     * @return
     */
    private static boolean lineFitsDemands(String line) {
        String[] tok=line.split("\t");
        if(tok[1].equals("2")==false)
            return false;
        if(tok[2].equals("0")==false)
            return false;
        if(tok[3].equals("1")==false)
            return false;
        if(tok[6].toLowerCase().equals("un")==true)
            return false;
        if(tok[16].trim().length()==0 || tok[16].equals("0"))
            return false;
        return true;
    }

}
