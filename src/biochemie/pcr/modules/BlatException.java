/*
 * Created on 25.11.2004
 *
 */
package biochemie.pcr.modules;


/**
 * @author Steffen Dienst
 *
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
