package mikera.util;

import java.util.*;

/**
 * List using a static array
 * 
 * @author Mike
 *
 * @param <T>
 */
public class StaticArrayList<T> extends AbstractList<T> implements Set<T> {

	Object[] array;
	
	public StaticArrayList(Object[] referencedArray) {
		array=referencedArray;
	}
	
	public StaticArrayList(Collection<T> collection) {
		array=collection.toArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		return (T)array[index];
	}

	@Override
	public int size() {
		return array.length;
	}

}
