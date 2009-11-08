package mikera.util.emptyobjects;

import java.util.*;

import mikera.persistent.*;

public final class NullSet<T> extends NullCollection<T> implements Set<T> {
	private static final long serialVersionUID = -6170277533575154354L;
	
	@SuppressWarnings("unchecked")
	public static NullSet<?> INSTANCE=new NullSet();
	
	private NullSet() {
		
	}
}