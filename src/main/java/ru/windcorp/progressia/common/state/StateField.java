package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.util.Namespaced;

public abstract class StateField extends Namespaced {
	
	private final boolean isLocal;
	private final int index;

	public StateField(
			String namespace, String name,
			boolean isLocal,
			int index
	) {
		super(namespace, name);
		this.isLocal = isLocal;
		this.index = index;
	}
	
	public boolean isLocal() {
		return isLocal;
	}
	
	protected int getIndex() {
		return index;
	}
	
	public abstract void read(
			StatefulObject object,
			DataInput input,
			IOContext context
	) throws IOException;
	
	public abstract void write(
			StatefulObject object,
			DataOutput output,
			IOContext context
	) throws IOException;
	
	public abstract void copy(StatefulObject from, StatefulObject to);
	
	public abstract int computeHashCode(StatefulObject object);
	
	public abstract boolean areEqual(StatefulObject a, StatefulObject b);

}
