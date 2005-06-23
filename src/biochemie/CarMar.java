package biochemie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CarMar {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.out.println("Aufruf: java.exe -jar carma.jar eingabe1 eingabe2... ausgabe");
            System.exit(0);
        }
        BufferedReader[] br = new BufferedReader[args.length-1];
        for (int i = 0; i < br.length; i++) {
            br[i]=new BufferedReader(new FileReader(args[i]));
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[args.length-1]));
        //int count=0;
        while(true) {
            int c = 0;
            boolean outputSmall = true;
            //System.out.println(++count);
            for (int i = 0; i < br.length; i++) {
                c= br[i].read();
                //System.out.println(c);
                if(c == -1) {
                    for (int j = 0; j < br.length; j++) {
                        br[j].close();
                    }
                    bw.close();
                    System.exit(0);
                }
                if(Character.isUpperCase((char) c))
                    outputSmall=false;
            }
            
            if(outputSmall)
                bw.write(Character.toLowerCase((char) c));
            else
                bw.write(Character.toUpperCase((char) c));
        }
    }

}
