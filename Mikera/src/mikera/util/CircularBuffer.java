package mikera.util;

import java.util.*;

/**
 * Circular Buffer implementation
 * 
 * Supports nulls, variable size buffer, fast random access reads
 * 
 * @author Mike
 *
 * @param <V> Type of object contained in buffer
 */
public class CircularBuffer<V> extends AbstractCollection<V> implements Queue<V> {
	// ArrayList size is always less than or equal to maxSize
	// arraylist size equal to maxSize if buffer is full, or if there is any wraparound
	private int maxSize;
	private ArrayList<V> values=new ArrayList<V>();
	
	private int end=0;
	private int count=0;
	
	/**
	 * Construct a new, empty circular buffer
	 * 
	 * @param n Capacity of buffer
	 */
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
	
	/**
	 * Adds the value only if the queue has spare capacity
	 */
	public boolean offer(V value) {
		if (count<maxSize) {
			return add(value);
		} else {
			return false;
		}
	}
	
	/**
	 * Unconditionally adds a value to the buffer
	 * If full, first added item will be deleted to make space
	 */
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
	
	/**
	 * Remove end value (i.e. least recently added
	 * @return true if value removes, false if buffer is empty
	 */
	public boolean tryRemoveEnd() {
		if (count>0) {
			count-=1;
			return true;
		} else {
			return false;
		}
	}
	
	public V remove() {
		if (count==0) throw new NoSuchElementException("Empty CircularBuffer in CircularBufer.remove()");
		return removeFirstAdded();
	}
	
	public V poll() {
		return removeFirstAdded();
	}
	
	public V removeFirstAdded() {
		if (count>0) {
			V value=get(count-1);
			count-=1;
			return value;
		} else {
			return null;
		}
	}
	
	public V removeLastAdded() {
		if (count>0) {
			V value=element();
			count-=1;
			end+=1;
			return value;
		} else {
			return null;
		}
	}
	
	public V element() {
		if (count==0) throw new NoSuchElementException("Empty CircularBuffer in CircularBufer.element()");
		return values.get(firstAddedIndex());
	}
	
	public V peek() {
		if (count<=0) return null;
		return values.get(firstAddedIndex());
	}
	
	private int lastAddedIndex() {
		if (end>0) return end-1;
		return values.size()-1; // must be last index, since end=0, or returns -1 if buffer empty
	}
	
	private int firstAddedIndex() {
		if (count==0) return -1;
		return positionIndex(count-1); 
	}
	
	private int positionIndex(int n) {
		if (n>=count) return -1;
		int i=end-n-1;
		if (i<0) i+=maxSize;
		return i;
	}
	
	public V getLastAdded() {
		int i=lastAddedIndex();
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
		if (n<0) throw new NoSuchElementException("Negative index in CircularBuffer.get(int) not allowed");
		int i=positionIndex(n);
		return values.get(i);
	}
}
