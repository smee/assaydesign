/*
 * Created on 02.08.2004 by Steffen
 *
 */
package biochemie.util;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Steffen
 * 02.08.2004
 */
public class Stack{
    
    LinkedList ll;
    public Stack() {
        ll=new LinkedList();
    }
    
    public void push(Object obj) {
        ll.addLast(obj);
    }
    public Object top() {
        if(0 == ll.size())
            return null;
        return ll.getLast();
    }
    public Object pop() {
        if(0 == ll.size())
            return null;
        Object obj = ll.getLast();
        ll.removeLast();
        return obj;
    }
    public Object[] getTopMostTwo() {
        if(2 > ll.size())
            return null;
        return new Object[] {ll.get(ll.size()-2),ll.getLast()};
    }
    /**
     * @return
     */
    public int size() {
        return ll.size();
    }
    
    public Iterator iterator() {
        return ll.iterator();
    }
}
