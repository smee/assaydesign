package biochemie.haplo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BestTransformer {

    public BestTransformer() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if(args.length==0) {
            showUsage();
            System.exit(0);
        }
        File f=new File(args[0]);
        if(!f.exists()) {
            System.err.println("File \""+args[0]+"\" doesn't exist.");
            System.exit(0);
        }
        process(f);
        System.out.println("Done.");
    }

    private static void process(File f) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader(f));
        String newfile=f.getAbsolutePath()+".txt";
        System.out.println("Writing results to "+newfile+"...");
        BufferedWriter bw=new BufferedWriter(new FileWriter(newfile));
        
        try {
            String line=br.readLine();
            final String firstline=br.readLine();
            bw.write(firstline);
            while((line=br.readLine())!=null) {
                if(line.length() == 0 || line.charAt(0)=='>')
                    continue;
                bw.write(adjustLine(firstline, line));
                bw.write('\n');
            }
        }finally {
            bw.close();
            br.close();
        }
    }

    private static String adjustLine(String orig, String line) {
        if(orig.length()!=line.length()) {
            
        }
        StringBuffer res=new StringBuffer(line.length());
        for(int i=0;i<orig.length();i++)
            res.append(adjustChar(orig.charAt(i),line.charAt(i)));
        return res.toString();
    }

    private static String adjustChar(char orig, char c) {
        switch (c) {
        case '-'://identical
            return Character.toString(orig);
        case '*'://not sequenced
            return Character.toString(orig);
        case '.'://deletion
            return "";
        default:
            return Character.toString(c);
        }
    }

    private static void showUsage() {
        System.out.println("Usage:\n------\n");
        System.out.println("besttransformer.exe fastafile");
    }

}
