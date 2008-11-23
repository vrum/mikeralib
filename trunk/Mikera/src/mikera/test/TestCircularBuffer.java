package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;
import java.util.*;

public class TestCircularBuffer {
	@Test public void test1() {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(10);
		
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
		
		cb.setMaxSize(2);
		assertEquals(2,cb.getCount());
		assertEquals(106,cb.get(0));
		assertEquals(105,cb.get(1));
		assertEquals(null,cb.get(2));
	}
	
	@Test public void test2() {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(10);
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
	}
	
	@Test public void test3() {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(10);
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
}
