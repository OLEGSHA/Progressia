package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntStateField extends StateField {

	public IntStateField(
			String namespace, String name,
			boolean isLocal,
			int index
	) {
		super(namespace, name, isLocal, index);
	}
	
	public int get(StatefulObject object) {
		return object.getStorage().getInt(getIndex());
	}
	
	public void setNow(StatefulObject object, int value) {
		object.getStorage().setInt(getIndex(), value);
	}
	
	public void set(StateChanger changer, int value) {
		changer.setInt(this, value);
	}

	@Override
	public void read(
			StatefulObject object,
			DataInput input,
			IOContext context
	) throws IOException {
		object.getStorage().setInt(getIndex(), input.readInt());
	}

	@Override
	public void write(
			StatefulObject object,
			DataOutput output,
			IOContext context
	) throws IOException {
		output.writeInt(object.getStorage().getInt(getIndex()));
	}

	@Override
	public void copy(StatefulObject from, StatefulObject to) {
		setNow(to, get(from));
	}

	@Override
	public int computeHashCode(StatefulObject object) {
		return get(object);
	}

	@Override
	public boolean areEqual(StatefulObject a, StatefulObject b) {
		return get(a) == get(b);
	}

}
