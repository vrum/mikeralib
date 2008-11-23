package mikera.util;

public class VectorArray {
	public static float squareDistance(float[] a, float[] b) {
		float eSquared=0;
		for (int i=0; (i<a.length); i++) {
			float d=a[i]=b[i];
			eSquared+=d*d;
		}
		return eSquared;
	}
}
