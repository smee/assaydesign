package biochemie;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class MyAssert{

    public static void assertEquals(List actual, List value){
        TestCase.assertEquals(actual.size(),value.size());
        Iterator it=value.iterator();
        Iterator it2 = actual.iterator();
        for (; it.hasNext() && it2.hasNext();) {
            TestCase.assertEquals(it2.next(),it.next());
        }
    }
    public static void assertEquals(Object[] actual, Object[] value){
        TestCase.assertEquals(actual.length,value.length);
        for (int i = 0; i < actual.length; i++) {
            TestCase.assertEquals(actual[i],value[i]);
        }
    }
    public static void assertEquals(int[] actual, int[] value){
        TestCase.assertEquals(actual.length,value.length);
        for (int i = 0; i < actual.length; i++) {
            TestCase.assertEquals(actual[i],value[i]);
        }
    }
    public static void assertEquals(float[] actual, float[] value, float diff){
        TestCase.assertEquals(actual.length,value.length);
        for (int i = 0; i < actual.length; i++) {
            TestCase.assertEquals(actual[i],value[i],diff);
        }
    }
    public static void assertEquals(double[] actual, double[] value, double diff){
        TestCase.assertEquals(actual.length,value.length);
        for (int i = 0; i < actual.length; i++) {
            TestCase.assertEquals(actual[i],value[i],diff);
        }
    }
}
