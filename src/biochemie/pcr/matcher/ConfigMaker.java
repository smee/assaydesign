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
            protected String[] getInitializedProperties() {
                return vals[0];
            }
        };
        for (int i = 0; i < vals[0].length; i++) {
            conf.setProperty(vals[0][i],vals[ptr][i]);
        }
        ptr++;
        return conf;
    }
    public int getNumOfConfigsLeft() {
        return vals.length-ptr-1;
    }
}
