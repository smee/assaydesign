/*
 * Created on 01.12.2003
 *
 */
package biochemie.sbe;

import java.lang.reflect.Field;

import junit.framework.TestCase;
import biochemie.sbe.calculators.Combination;

/**
 *
 * @author Steffen
 *
 */
public class CombinationTest extends TestCase {
    /**
     * Constructor for CombinationTest.
     * @param arg0
     */
    public CombinationTest(String arg0) {
        super(arg0);
    }
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CombinationTest.class);
    }
    public void testGetNextCombination() {
        Combination comb=new Combination(10,5);
        int[]array;
        do{
            array=comb.getNextCombination();
            assertEquals(5,array.length);
        }while(comb.hasNext());
        try {
            comb.getNextCombination();
            fail();
        } catch (IllegalStateException e) {}        
    }
    
    public void testGetNextWOComb(){
		try {
			Combination comb=new Combination(10,5);
			Field f=comb.getClass().getDeclaredField("aktcomb");
			f.setAccessible(true);
			f.set(comb,new int[]{1,2,3,4,5});
			int[] c=comb.getNextCombinationWithout(2);
			int[] erg={1,3,4,5,6};
			for (int i= 0; i < erg.length; i++) {
			    assertEquals(erg[i],c[i]);
			}
			f.set(comb,new int[]{1,2,3,8,9});
			c=comb.getNextCombinationWithout(8);
			erg=new int[]{1,2,4,5,6};
			for (int i= 0; i < erg.length; i++) {
			    assertEquals(erg[i],c[i]);
			}
			f.set(comb,new int[]{1,2,4,7,8});
			c=comb.getNextCombinationWithout(0);
			erg=new int[]{1,2,4,7,9};
			for (int i= 0; i < erg.length; i++) {
			    assertEquals(erg[i],c[i]);
			}
			f.set(comb,new int[]{1,2,4,7,9});
			c=comb.getNextCombinationWithout(8);
			erg=new int[]{1,2,5,6,7};
			for (int i= 0; i < erg.length; i++) {
			    assertEquals(erg[i],c[i]);
			}
		} catch (Exception e) {
			fail();
		}
    }
}
