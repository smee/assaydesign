package biochemie.haplo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BestTransformer {
    static interface LineProcessor{
        String process(String line);
    }
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
        final StringBuffer orig=new StringBuffer(); 
        process(f,File.createTempFile("__best","tmp"),new LineProcessor() {
            public String process(String line) {
                if(orig.length()==0)
                    orig.append(line);
                else
                    for(int i=0; i<line.length();i++)
                        if(line.charAt(i)=='.')
                            orig.setCharAt(i,'x');//mark for deletion
                return line;
            }
            
        });
        String newf=f.getAbsolutePath()+".result.txt";
        System.out.println("Writing results to \""+newf+"\"...");
        process(f,new File(newf),new LineProcessor() {
            public String process(String line) {
                StringBuffer sb=new StringBuffer(line.length());
                for(int i=0;i<line.length();i++) {
                    if(orig.charAt(i)=='x')
                        continue;
                    sb.append(adjustChar(orig.charAt(i),line.charAt(i)));
                }
                return new String(sb);
            }
        });
        System.out.println("Done.");
    }

    private static void process(File f, File newf, LineProcessor proc) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader(f));
        BufferedWriter bw=new BufferedWriter(new FileWriter(newf));
        try {
            String line=br.readLine();            
            while((line=br.readLine())!=null) {
                if(line.length() == 0 || line.charAt(0)=='>')
                    continue;
                bw.write(proc.process(line));
                bw.write('\n');
            }
        }finally {
            bw.close();
            br.close();            
        }
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
            if(orig == '.')
                return "";
            else
                return Character.toString(c);
        }
    }

    private static void showUsage() {
        System.out.println("Usage:\n------\n");
        System.out.println("besttransformer.exe fastafile");
    }

}
