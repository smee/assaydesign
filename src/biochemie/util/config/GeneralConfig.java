/*
 * Created on 18.09.2003
 *
*/
package biochemie.util.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;

import biochemie.sbe.WrongValueException;

/**
 * @author Steffen
 *
 */
public abstract class GeneralConfig extends Observable{
	
	protected final Properties prop;
    private final Set keys;
    ;
    public GeneralConfig(){
        prop = new Properties();
        String[][] s = getInitializedProperties();
        
        keys = new HashSet();
        
        for (int i = 0; i < s.length; i++) {
            keys.add(s[i][0]);
            prop.setProperty(s[i][0],s[i][1]);
        }
    }
    /**
     * Every subclass needs to provide all valid keys and defaultvalues on instantiation in this method.
     * No keys will be accepted later on if they can't be found in this instance of map.
     * Needs to hav 2 columns: key and value
     * @return
     */
	abstract protected String[][] getInitializedProperties();
	/**
     * Der INputstream wird nicht geschlossen, nachdem er verwendet wurde!
	 * @param instream
	 */
	public synchronized void readConfigFile(String filename)  throws IOException{
		FileInputStream fin=new FileInputStream(filename);	
        Properties ptemp = new Properties();
        ptemp.load( fin );
        fin.close();
        Enumeration e = ptemp.propertyNames();
        while(e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if(isValidKey(key))
                prop.setProperty(key,ptemp.getProperty(key));
        }
	}

    public void writeConfigTo(String filename) throws IOException {    
        FileOutputStream fos= new FileOutputStream(filename);
        prop.store(fos,null);
        fos.close();
    }
    
	/**
	 * Reads configvalues and updates them if needed. Comments won't be erased.
	 * @param filename
	 */
    public void updateConfigFile(String filename)  throws IOException{
       	
            BufferedReader in= new BufferedReader(new FileReader(filename));
            StringBuffer sb= new StringBuffer();
            String temp,line;
            
            Set s = new HashSet(prop.keySet()); //alle mir bekannten Parameter
            
            while((line = in.readLine()) != null) {
            	temp=line.trim();
                if (!temp.startsWith("#") && temp.indexOf('=') != -1) {
                    temp= temp.substring(0, temp.indexOf('=')).trim();
                    if(temp.length() > 0 && isValidKey(temp)){
                    	sb.append(temp + '=');
                    	sb.append(prop.getProperty(temp) + '\n');
                        s.remove(temp);//hat mer schon
                    }else {//mir nicht bekannter parameter
                        sb.append(line + '\n');
                    }
                } else {//kommentar oder unsinnige zeile, kein Parameter
                    sb.append(line + '\n');
                }
            }
            in.close();
            //add remaining Parameters
            for (Iterator it = s.iterator(); it.hasNext();) {
                String key = (String) it.next();
                sb.append(key); sb.append('=');
                temp = prop.getProperty(key);
                if(temp != null)
                    sb.append(temp);
                sb.append('\n');
            }
            BufferedWriter out= new BufferedWriter(new FileWriter(filename));
            out.write(sb.toString());
            out.close();
    }
    /**
     * Liefert den Wert des Eintrags mit dem Key <code>key</code>.
     * @param key
     * @return String, wenn der Schlüssel nicht existiert, wird <code>null</code> geliefert.
     */
    public String getString(String key){
        return prop.getProperty(key);
    }
    public int getInteger(String key) throws WrongValueException{
        String val=getString(key);
        if(null == val)
            throw new WrongValueException("Schlüssel '"+key+"' nicht vorhanden!");
        try{
            int ival=Integer.parseInt(val);
            return ival;
        }catch (NumberFormatException e) {
            throw new WrongValueException("Der Wert '"+val+"' ist keine ganze Zahl!");
        }
    }
    public float getFloat(String key) throws WrongValueException{
        String val=getString(key);
        if(null == val)
            throw new WrongValueException("Schlüssel '"+key+"' nicht vorhanden!");
        try{
            float fval=Float.parseFloat(val);
            return fval;
        }catch (NumberFormatException e) {
            throw new WrongValueException("Der Wert '"+val+"' ist keine Zahl!");
        }
    }
    public double getDouble(String key) throws WrongValueException{
        String val=getString(key);
        if(null == val)
            throw new WrongValueException("Schlüssel '"+key+"' nicht vorhanden!");
        try{
            double dval=Double.parseDouble(val);
            return dval;
        }catch (NumberFormatException e) {
            throw new WrongValueException("Der Wert '"+val+"' ist keine Zahl!");
        }
    }
    public boolean getBoolean(String key) throws WrongValueException{
        String val=getString(key);
        if(null == val)
            throw new WrongValueException("Schlüssel '"+key+"' nicht vorhanden!");
        try{
            boolean bval=Boolean.valueOf(val).booleanValue();
            return bval;
        }catch (NumberFormatException e) {
            throw new WrongValueException("Der Wert '"+val+"' ist weder 'true' noch 'false'");
        }
    }
    public boolean getBoolean(String key, boolean deflt){
    	try{
    		return getBoolean(key);
    	}catch(WrongValueException e){
    		return deflt;
    	}
    }
    public int getInteger(String key, int deflt){
    	try{
    		return getInteger(key);
    	}catch(WrongValueException e){
    		return deflt;
    	}
    }
    public float getFloat(String key, float deflt){
    	try{
    		return getFloat(key);
    	}catch(WrongValueException e){
    		return deflt;
    	}
    }
    public double getDouble(String key, double deflt){
    	try{
    		return getDouble(key);
    	}catch(WrongValueException e){
    		return deflt;
    	}
    }
    public String getString(String key, String deflt){
    	String ret = getString(key);
    	if( ret == null)
    		ret = deflt;
    	
    	return ret;
    }
    /**
     * Returns true if the key describes a valid property of this instance.
     * @param key
     * @return
     */
    protected boolean isValidKey(String key) {
        return keys.contains(key);
    }
	/**
     * TODO sollte protected sein, sobald alles schoen geinterfact wurde :)
	 * @param string
	 * @param string2
	 */
    public void setProperty(String key, String value) {
        if(!isValidKey(key))
            return;
		prop.setProperty(key, value);		
        setChanged();
        notifyObservers(key);
	}
    /**
	 * @param string
	 * @return
	 */
    public String getProperty(String string) {
		return prop.getProperty(string);
	}
}
