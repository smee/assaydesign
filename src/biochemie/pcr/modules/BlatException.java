/*
 * Created on 25.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package biochemie.pcr.modules;


/**
 * @author Steffen Dienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BlatException extends RuntimeException{

	/**
	 * @param string
	 */
	public BlatException(String string) {
		super(string);
	}

	/**
	 * @param e
	 */
	public BlatException(Exception e) {
		super(e);
	}

}
