/*
 * Created on 18.11.2004
 *
 */
package biochemie.sbe.gui;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.util.Helper;

/**
 * Like PBSequenceField with a possible L within.
 * @author Steffen Dienst
 *
 */
public class SBESequenceTextField extends PBSequenceField  {



	public SBESequenceTextField(){
		super(100,true,"ACGTLacgt");
		setUniqueChars("L");
	}



	public String getSequence(){
        if(isEnabled())
            return Helper.getNuklFromString(super.getText().replace('L','A'));
        return "";
	}
	/* (non-Javadoc)
	 * @see biochemie.calcdalton.gui.PBSequenceField#setValidChars(java.lang.String)
	 */
	public void setValidChars(String validChars) {
		if(validChars.indexOf('L')== -1)
			validChars+='L';
		super.setValidChars(validChars);
	}

}
