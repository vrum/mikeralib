package mikera.util;

import java.util.*;

public class CircularBuffer<V> extends AbstractCollection<V> {
	// arraylist size is always less than or equal to maxSize
	private int maxSize;
	private ArrayList<V> values=new ArrayList<V>();
	
	private int end=0;
	private int count=0;
	
	public CircularBuffer(int n) {
		setMaxSize(n);
	}
	
	public CircularBuffer() {
		setMaxSize(10);
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public int size() {
		return count;
	}
	
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			int pos=0;
			
			public boolean hasNext() {
				return (pos<count);
			}
			
			public V next() {
				V value=get(pos);
				pos++;
				return value;
			}
			
			public void remove() {
				if (pos<=0) return; // not yet called
				CircularBuffer.this.remove(get(pos-1));
			}
		};
	}
	
	public void setMaxSize(int n) {
		int vs=values.size();
		if (n>=vs) {
			// extend
			// shifting to add nulls if needed (i.e. if there is wrap around)
			// otherwise no need to resize - will grow automatically
			int shift=Math.max(0, count-end);
			if (shift>0) {
				int add=n-vs; // number of nulls to add
				values.ensureCapacity(n);
				for (int i=0; i<add; i++) {
					values.add(null);
				}
				
				for (int i=n-1; i>n-1-shift; i--) {
					values.set(i,values.get(i-add));
					values.set(i-add,null);
				}
			}
			
			maxSize=n;
			return;
		} else {
			// shrink
			int overlap=Math.max(0, (vs-n)-(vs-end)); // number of valid items rolling around at end of values
			int cut=Math.min(vs-end, vs-n);
			if ((cut+overlap)!=(vs-n)) throw new Error("Unexpected....");
			
			if (overlap>0) {
				// shuffle items back to to start or array
				for (int i=0; i<end-overlap; i++) {
					values.set(i,values.get(i+overlap));
				}	
				end-=overlap;
			} else {
				// pull items from end of values
				for (int i=0; i<vs-end-cut; i++) {
					values.set(end-overlap+i,values.get(end+i));
				}
			}
			
			// shorten array
			for (int i=vs-1; i>=(vs-(cut+overlap)); i--) {
				values.remove(i);
			}
			
			maxSize=n;
			count=Math.min(count,n); // number removed
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean add(V value) {
		if (maxSize<=0) return false;
		
		int vs=values.size();
		if (vs<maxSize) {
			// append
			values.add(value);
			end++;
			count++;
		} else {
			// cycle and replace old elements
			if (end==vs) end=0;
			values.set(end,value);
			end++;
			if (count<maxSize) count++;
			if (end>=vs) end=0;
		}
		return true;
	}
	
	public void clear() {
		count=0;
	}
	
	private int lastIndex() {
		if (end>0) return end-1;
		return count-1; // must be last index, since end=0, or returns -1 if buffer empty
	}
	
	public V getLast(int n) {
		int i=lastIndex();
		if (i<0) return null;
		return values.get(i);
	}
	
	public int getCount() {
		return count;
	}
	
	/**
	 * Get an item from the circular buffer
	 * 
	 * @param n Index of item in circular buffer (0 = most recently added, getCount()-1 = last item)
	 * @return Value at position n from front of buffer
	 */
	public V get(int n) {
		if (n>=count) return null;
		int i=end-n-1;
		if (i<0) i+=values.size(); // wrap around
		return values.get(i);
	}
}
