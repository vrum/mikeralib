package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;

public class TestBits {
	@Test public void testRoundUp() {
		assertEquals(16,Bits.roundUpToPowerOfTwo(15));
		assertEquals(16,Bits.roundUpToPowerOfTwo(16));
		assertEquals(32,Bits.roundUpToPowerOfTwo(17));
		assertEquals(1,Bits.roundUpToPowerOfTwo(1));
		assertEquals(0,Bits.roundUpToPowerOfTwo(0));
	}
	
	@Test public void testRoundDown() {
		assertEquals(16,Bits.roundDownToPowerOfTwo(17));
		assertEquals(16,Bits.roundDownToPowerOfTwo(31));
		assertEquals(32,Bits.roundDownToPowerOfTwo(33));
		assertEquals(2,Bits.roundDownToPowerOfTwo(2));
		assertEquals(1,Bits.roundDownToPowerOfTwo(1));
		assertEquals(0,Bits.roundDownToPowerOfTwo(0));
	}
}
