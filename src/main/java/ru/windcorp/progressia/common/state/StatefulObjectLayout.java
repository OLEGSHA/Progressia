package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class StatefulObjectLayout {
	
	private final String objectId;

	public StatefulObjectLayout(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public abstract StateStorage createStorage();
	
	protected void checkObject(StatefulObject object) {
		if (!object.getId().equals(getObjectId())) {
			throw new IllegalArgumentException(
					object.getId() + " is not " + getObjectId()
			);
		}
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
	
	public abstract StateFieldBuilder getBuilder(String namespace, String name);

}
