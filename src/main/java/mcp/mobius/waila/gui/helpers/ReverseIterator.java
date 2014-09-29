package mcp.mobius.waila.gui.helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;

public class ReverseIterator<T> implements Iterable<T> {

    private ListIterator<T> listIterator;        

    public ReverseIterator(Collection<T> wrappedList) {
    	List list = Lists.newArrayList(wrappedList);
        this.listIterator = list.listIterator(wrappedList.size());            
    }               

    public Iterator<T> iterator() {
        return new Iterator<T>() {

            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            public T next() {
                return listIterator.previous();
            }

            public void remove() {
                listIterator.remove();
            }

        };
    }	
	
}
