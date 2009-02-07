package mikera.util;

public class Pair<A,B> implements Cloneable, Comparable<Pair<A,B>> {
	public A a;
	public B b;
	
	public boolean equals(Pair<A,B> p) {
		if (p==null) return false;
		
		if (a==null) {
			if (p.a!=null) return false;
		} else {
			if (!a.equals(p.a)) return false;
		}
		
		if (b==null) {
			if (p.b!=null) return false;
		} else {
			if (!b.equals(p.b)) return false;
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	public int compareTo(Pair<A,B> p) {
		if (a instanceof Comparable) {
			if (a!=null) {
				Comparable c=(Comparable)a;
				int v=c.compareTo((Comparable)p.a);
				if (v!=0) return v;
			}	
		} else {
			throw new Error("Can't compare Pair: first component type not comparable");
		}
		
		if (b instanceof Comparable) {
			if (b!=null) {
				Comparable c=(Comparable)b;
				int v=c.compareTo((Comparable)p.b);
				if (v!=0) return v;
			}	
		} else {
			throw new Error("Can't compare Pair: second component type not comparable");
		}
		return 0;
	}
	
}
