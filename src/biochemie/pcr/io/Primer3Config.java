/*
 * Created on 18.09.2003
 *
*/
package biochemie.pcr.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import biochemie.util.config.GeneralConfig;


/**
 * Liest Primer3-Configfile ein und speichert alles als  Properties-Object.
 * @author Steffen
 *
 */
public class Primer3Config extends GeneralConfig {

	public Primer3Config(String filename) throws IOException {
		readConfigFile(filename);
		prop.remove("");//= entfernen
	}

	public void updateConfigFile(String filename) throws IOException {
		super.updateConfigFile(filename);
		try {
			BufferedWriter out=new BufferedWriter(new FileWriter(filename,true));
			out.write("=\n");
			out.close();
		} catch (IOException e) {
			UI.errorDisplay("Fehler beim Schreiben auf "+filename);
		}
	}

	protected String[][] getInitializedProperties() {
	    return new String[][]{
	            {"SEQUENCE","A"}
                ,{"PRIMER_NUM_RETURN","1000"}
                ,{"PRIMER_MAX_POLY_X",""}
                ,{"PRIMER_PRODUCT_SIZE_RANGE",""}
                ,{"PRIMER_MISPRIMING_LIBRARY",""}
                ,{"INFILE","test.in"}
                ,{"TARGET",""}
                };
	}
}
