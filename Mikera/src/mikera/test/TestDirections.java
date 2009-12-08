package mikera.test;

import org.junit.*;
import static org.junit.Assert.*;
import mikera.util.*;
import mikera.engine.*;

public class TestDirections {
	@Test public void testDirArrays() {
		byte[] td=new byte[27];
		
		for (int i=0; i<Dir.ALL_DIRECTIONS_3D.length; i++) {
			td[Dir.ALL_DIRECTIONS_3D[i]]++;
		}
		for (int i=0; i<Dir.DISTORDER_DIRECTIONS.length; i++) {
			td[Dir.DISTORDER_DIRECTIONS[i]]++;
		}
		
		for (int i=0; i<td.length; i++) {
			assertEquals(100*i+2, 100*i+td[i]);
		}
	}
	
	@Test public void testDirDirections() {
		Octreap<Integer> o=new Octreap<Integer>();
		
		for (int i=0; i<Dir.ALL_DIRECTIONS_3D.length; i++) {
			int d=Dir.ALL_DIRECTIONS_3D[i];
			int dx=Dir.DX[d];
			int dy=Dir.DY[d];
			int dz=Dir.DZ[d];
			o.set(dx,dy,dz,d);
		}
		assertEquals(27,o.countArea());
		
		// check distances are ordered
		for (int i=0; i<Dir.DISTORDER_DIRECTIONS.length; i++) {
			if (i>0) assertTrue(Dir.DIST[Dir.DISTORDER_DIRECTIONS[i]]>=Dir.DIST[Dir.DISTORDER_DIRECTIONS[i-1]]);
		}
		

		
	}
	
	@Test public void testDirCalcs() {
		for (int i=0; i<Dir.ALL_DIRECTIONS_3D.length; i++) {
			int d=Dir.ALL_DIRECTIONS_3D[i];
			int dx=Dir.DX[d];
			int dy=Dir.DY[d];
			int dz=Dir.DZ[d];

			int dcalc=Dir.getDir(dx, dy, dz);
			
			assertEquals(d,dcalc);
		}
	}
	
	@Test public void testPathFinding() {
		PathFinder pf=new PathFinder();
		
		final TreeGrid<Float> costs=new TreeGrid<Float>();
		pf.setCostFunction(new PathFinder.CostFunction() {
			@Override
			public float moveCost(int x, int y, int z, int tx, int ty, int tz) {
				Float f=costs.get(tx, ty, tz);
				if (f==null) return -1;
				return f.floatValue();
			}
		});
		costs.setBlock(0,0,0,10,10,0, 1.0f); // area
		costs.setBlock(5,0,0,5,9,0, -1.0f); // wall 1
		costs.setBlock(7,1,0,7,10,0, -1.0f); // wall 2
		pf.pathFind(0, 0, 0, 10,10,0);
		//System.out.println("Nodes: "+pf.nodeCount);
		//System.out.println("Costs: "+pf.costCount);
		assertTrue(pf.isFound());
		assertEquals(30,pf.foundNode().travelled,0.01f);
		
		costs.setBlock(5,0,0,5,10,0, -1.0f); // full wall
		pf.pathFind(0, 0, 0, 10,10,0);
		assertTrue(!pf.isFound());
		//System.out.println("Nodes: "+pf.nodeCount);
		//System.out.println("Costs: "+pf.costCount);
		
	}
}
