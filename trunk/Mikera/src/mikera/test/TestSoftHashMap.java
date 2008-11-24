package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;

public class TestSoftHashMap {
	@Test public void test1() {
		SoftHashMap<Integer,String> sh=new SoftHashMap<Integer,String>();
		
		sh.put(1,"dfevev");
		sh.put(2,"gbnfrvev");
		sh.put(3,"nhvev");
		sh.put(4,"hntmev");
		sh.put(5,"mtyuytmev");
		
		assertEquals("mtyuytmev",sh.get(5));
		
		assertEquals(5,sh.size());
		sh.clear();
		assertEquals(0,sh.size());
		assertEquals(null,sh.get(5));
	}
}
