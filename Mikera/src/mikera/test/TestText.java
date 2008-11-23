package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;

public class TestText {
	@Test public void testWhiteSpace() {
		assertEquals("   ",Text.createWhiteSpace(3));
		
		int n=Rand.d(10,10);
		assertEquals(n,Text.createWhiteSpace(n).length());
		
	}
	
	@Test public void testRoman() {
		assertEquals("XXXIV",Text.roman(34));
		
		assertEquals("-DCLXVI",Text.roman(-666));
		
		assertEquals("MMMCMXCIX",Text.roman(3999));
		
		assertEquals("nullus",Text.roman(0));
	}
}
