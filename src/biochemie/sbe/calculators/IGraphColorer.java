/*
 * Created on 03.08.2004 by Steffen
 *
 */
package biochemie.sbe.calculators;

/**
 * @author Steffen
 * 03.08.2004
 */
public interface IGraphColorer {
	public void start();
	public int[] getMinimalColoring();
	public int usedColors();
}
