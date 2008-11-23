package mikera.util;

import java.util.*;
import java.lang.ref.*;

/**
 * Hash map using soft references
 * 
 * @author Mike
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class SoftHashMap<K,V> extends AbstractMap<K,V> {
	private final Map<K,SoftReference<V>> data = new HashMap<K,SoftReference<V>>();
	
	/**
	 * Try keep a small number of references at a minimum in a circular buffer
	 * Contains most recently accesses items
	 */
	private final CircularBuffer<V> hardReferenceBuffer;

	public SoftHashMap() {
		this(10);
	}

	public SoftHashMap(int minSizeRetained) {
		hardReferenceBuffer=new CircularBuffer<V>(minSizeRetained);
	}
	
	public int size() {
		return data.size();
	}

	public V get(Object key) {
		V value=null;
		SoftReference<V> reference = data.get(key);
		if (reference != null) {
			value = reference.get();
			if (value==null) {
				// remove the key as well
				data.remove(key);
				return null;
			} else {
				// add to recently accessed list
				hardReferenceBuffer.add(value);
			}
		}
		return value;
	}


	/**
	 * Call cleanup now and again to remove garbage collected references
	 */
	private void cleanUp() {
		// do this quite rarely
		if (Rand.r(data.size())>0) return;
		cleanUpNow();
	}
	
	private void cleanUpNow() {
		// check for cleared references and remove associated keys
		for (K key: data.keySet()) {
			SoftReference<V> value=data.get(key);
			if (value.get()==null) {
				data.remove(key);
			}
		}		
	}
	
	public V put(K key, V value) {	
		cleanUp(); 
		
		data.put(key, new SoftReference<V>(value));
		// add to recently accessed list
		hardReferenceBuffer.add(value);
		
		return value;
	}

	public V remove(Object key) {
		cleanUp(); 
		
		V value = data.remove(key).get();
		return value;
	}

	public void clear() {
		hardReferenceBuffer.clear();
		data.clear();
	}


	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new Error("SoftHashMap entrySet not supported");
	}
}
