package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;
import java.util.*;

public class TestCircularBuffer {
	
	// get a empty integer buffer with random position
	public CircularBuffer<Integer> getIntegerBuffer(int i) {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(i);
		
		for (int ii=Rand.r(10*i); ii>0; ii-- ) {
			cb.add(ii);
		}
		
		cb.clear();
		return cb;
	}
	
	@Test public void testRandomly() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		
		for (int i=0; i<100; i++) {
			if (Rand.d(3)==1) cb.setMaxSize(Rand.r(20));
			if (Rand.d(3)==1) cb.add(Rand.d(100));
			if (Rand.d(3)==1) cb.removeFirstAdded();
			if (Rand.d(3)==1) cb.removeLastAdded();
			if (Rand.d(3)==1) cb.poll();
		}
	}
	
	@Test public void testSize() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		
		for (int i=0; i<107; i++) {
			cb.add(i);
		}
		assertEquals(10,cb.getCount());
		assertEquals(106,cb.get(0));
		assertEquals(97,cb.get(9));
		assertEquals(null,cb.get(10));
		assertEquals(null,cb.get(1000));
		
		cb.setMaxSize(5);
		assertEquals(106,cb.get(0));
		assertEquals(102,cb.get(4));
		assertEquals(null,cb.get(5));		
		assertEquals(null,cb.get(1000));
		
		for (int i=0; i<65; i++) {
			cb.add(i);
		}
		assertEquals(64,cb.get(0));
		assertEquals(60,cb.get(4));
		assertEquals(null,cb.get(5));
		assertEquals(null,cb.get(1000));
		
		// expand buffer to 20
		cb.setMaxSize(20);
		assertEquals(5,cb.getCount());
		assertEquals(64,cb.get(0));
		assertEquals(60,cb.get(4));
		assertEquals(null,cb.get(5));
		assertEquals(null,cb.get(1000));
		
		for (int i=0; i<107; i++) {
			cb.add(i);
		}
		assertEquals(20,cb.getCount());
		assertEquals(106,cb.get(0));
		assertEquals(97,cb.get(9));
		assertEquals(87,cb.get(19));
		assertEquals(null,cb.get(20));
		assertEquals(null,cb.get(1000));
		
		// make very small
		cb.setMaxSize(2);
		assertEquals(2,cb.getCount());
		assertEquals(106,cb.get(0));
		assertEquals(105,cb.get(1));
		assertEquals(null,cb.get(2));
		
		// zero size buffer should also work!!
		cb.setMaxSize(0);
		assertEquals(0,cb.getCount());
		assertEquals(null,cb.get(10));
		assertEquals(null,cb.get(0));
		assertEquals(null,cb.peek());
		cb.add(100);
		assertEquals(0,cb.getCount());
		assertEquals(null,cb.poll());
		assertEquals(0,cb.getCount());
		
	}
	
	@Test public void testClear() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=0; i<8; i++) {
			cb.add(i);
		}
		cb.clear();
		assertEquals(null,cb.get(0));
		assertEquals(0,cb.getCount());
		for (int i=0; i<8; i++) {
			cb.add(i);
		}
		assertEquals(7,cb.get(0));
		assertEquals(8,cb.getCount());
		
		cb.clear();
		assertEquals(null,cb.get(0));
		assertEquals(0,cb.getCount());
	}
	
	@Test public void testIterator() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=0; i<20; i++) {
			cb.add(i);
		}

		int total=0;
		Iterator<Integer> it=cb.iterator();
		while (it.hasNext()) {
			total+=it.next();
		}
		assertEquals(145,total);
	}
	
	@Test public void testRemoveEnds() {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(10);
		for (int i=0; i<2; i++) {
			cb.add(i+3);
		}

		assertEquals(4,cb.get(0));
		assertEquals(3,cb.get(1));
		
		assertTrue(cb.tryRemoveEnd());
		assertEquals(1,cb.getCount());

		assertEquals(4,cb.get(0));
		assertEquals(null,cb.get(1));
		
		assertTrue(cb.tryRemoveEnd());		
		assertEquals(0,cb.getCount());
		assertFalse(cb.tryRemoveEnd());
		assertEquals(0,cb.getCount());
		
		cb.clear();
		for (int i=1; i<=10; i++) {
			cb.add(i);
		}
		assertEquals(10,cb.removeLastAdded());
		assertEquals(1,cb.removeFirstAdded());
		assertEquals(9,cb.removeLastAdded());
		assertEquals(2,cb.removeFirstAdded());
		assertEquals(6,cb.getCount());
		assertEquals(8,cb.removeLastAdded());
		assertEquals(3,cb.removeFirstAdded());
		assertEquals(7,cb.removeLastAdded());
		assertEquals(4,cb.removeFirstAdded());
		assertEquals(6,cb.removeLastAdded());
		assertEquals(5,cb.removeFirstAdded());
		assertEquals(0,cb.getCount());
		assertEquals(null,cb.removeLastAdded());
		assertEquals(null,cb.removeFirstAdded());
		assertEquals(0,cb.getCount());
	}
	
	@Test public void testRemove() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=1; i<=10; i++) {
			cb.add(i);
		}
		
		assertEquals(3,cb.removeRange(5,3));
		assertEquals(7,cb.size());
		
		assertEquals(10,cb.get(0));
		assertEquals(6,cb.get(4));
		assertEquals(2,cb.get(5));
		assertEquals(1,cb.get(6));
		assertEquals(null,cb.get(7));
		
		assertEquals(6,cb.remove(4));
		assertEquals(2,cb.remove(4));
		assertEquals(1,cb.remove(4));
		assertEquals(4,cb.size());
	}
	
	@Test public void testQueue() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=1; i<=10; i++) {
			cb.add(i);
		}

		assertEquals(false, cb.offer(15));
		
		assertEquals(10,cb.getCount());

		assertEquals(1,cb.peek());
		assertEquals(1,cb.element());
		
		assertEquals(1,cb.remove());
		assertEquals(2,cb.poll());
		assertEquals(8,cb.getCount());
		
		assertEquals(true, cb.offer(11));

		for (int i=3; i<=10; i++) {
			assertEquals(i,cb.poll());
		}
		
		assertEquals(11,cb.peek());
		assertEquals(11,cb.remove());
		assertEquals(null,cb.peek());
		assertEquals(0,cb.getCount());
		
	}
}
