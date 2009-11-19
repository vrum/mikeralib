package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;
import mikera.engine.*;

public class TestRand {
	@Test public void testD() {
		int[] rs=new int [100];
		
		for (int i=0; i<1000; i++) {
			rs[Rand.d(20)]++;
		}
		
		assertEquals(0,rs[0]);
		assertEquals(0,rs[21]);
		
		for (int i=1; i<=20; i++) {
			assertTrue(rs[i]>1);
		}
	}
	
	@Test public void testPo() {
		int r=0;
		
		for (int i=0; i<2000; i+=2) {
			r+=Rand.po(i);
		}
		assertTrue(r>900000);
		assertTrue(r<1100000);
	}
	
	@Test public void testGeom() {
		int[] hs=new int[100];
		int samples=1000;
		float p=0.4f;
		float targetmean=(1-p)/p;
		
		for (int i=0; i<samples; i++) {
			hs[Rand.geom(p)]++;
		}
		
		float sx=0;
		for (int i =0; i<hs.length; i++) {
			sx+=i*hs[i];
		}
		float mean=sx/samples;
		//System.out.println(mean);
		
		assertTrue(hs[0]>=hs[1]);
		assertTrue(mean>(targetmean*0.8));
		assertTrue(mean<(targetmean*1.2));
	}
	
	@Test public void testIntegerRandom() {
		assertTrue(1<=Rand.d6());
		assertTrue(0<=Rand.r(6));
		assertTrue(0<=Rand.r(2));
		assertTrue(6>=Rand.d6());
		assertTrue(5>=Rand.r(6));
		assertTrue(1>=Rand.r(2));

	}
	
	@Test public void testXORShoft() {
		long l=Rand.xorShift64(1L);
		assertTrue(l!=1);
		
		assertTrue(Rand.nextLong()!=0); // should never produce 0
	}
}