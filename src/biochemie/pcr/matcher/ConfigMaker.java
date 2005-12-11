package biochemie.pcr.matcher;

import java.io.FileReader;
import java.io.IOException;

import biochemie.util.Helper;
import biochemie.util.config.GeneralConfig;

import com.Ostermiller.util.BadDelimiterException;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.ExcelCSVParser;

public class ConfigMaker {
    String[][] vals;
    int ptr=1;
    
    public ConfigMaker(String filename) throws BadDelimiterException, IOException {
        char delim=Helper.getCSVDelimiterFrom(filename);
        ExcelCSVParser cp=new ExcelCSVParser(new FileReader(filename),delim);
        vals=cp.getAllValues();
        cp.close();
    }
    public GeneralConfig getNextConfig() {
        GeneralConfig conf=new GeneralConfig() {
            protected String[][] getInitializedProperties() {                
                String[][] ret=new String[vals[0].length][];
                for (int i = 0; i < ret.length; i++) {
                    ret[i]=new String[2];
                    ret[i][0]=vals[0][i];
                    ret[i][1]=vals[ptr][i];
                }
                ptr++;
                return ret;
            }
        };
        return conf;
    }
    public int getNumOfConfigsLeft() {
        return vals.length-ptr-1;
    }
}
