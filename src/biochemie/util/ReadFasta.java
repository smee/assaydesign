/*
 * Created on 25.10.2003
 *
 */
package biochemie.util;

/**
 *
 * @author Steffen
 *
 */
import java.io.IOException;

import biochemie.pcr.io.USCDParser;

public class ReadFasta {

  /**
   * The programs takes two args the first is the file name of the Fasta file.
   * The second is the name of the Alphabet. Acceptable names are DNA RNA or PROTEIN.
   */
  public static void main(String[] args) {
      //setup file input
      String filename = "uscd.txt";
      try {
        USCDParser uscdp=new USCDParser(filename);
        System.out.println(uscdp.getRepetetiveSeqsAsString());
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    }
}
