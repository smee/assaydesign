/*
 * Created on 25.10.2003
 *
 */
package biochemie.pcr.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Steffen
 *
 */
public class USCDParser {
    String sequence;
    
    public USCDParser(String filename) throws IOException {
        BufferedReader br= new BufferedReader(new FileReader(filename));
        String temp;
        StringBuffer sb=new StringBuffer();
        while(null != (temp = br.readLine())) {
            if('>' != temp.charAt(0))
                sb.append(temp);
        }
        sequence=sb.toString();
    }
    
    public String getRepetetiveSeqsAsString() {
        StringBuffer sb=new StringBuffer();
        int start=0;
        int end=0;
        int index=-1;
        while(index++!=sequence.length()-1) {
            if(Character.isLowerCase(sequence.charAt(index))) {
                start=index;
                while(index++!=sequence.length()-1) {
                    if(Character.isUpperCase(sequence.charAt(index))) {
                        end=index;
                        sb.append(""+start+','+(end-start)+' ');
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }
}
