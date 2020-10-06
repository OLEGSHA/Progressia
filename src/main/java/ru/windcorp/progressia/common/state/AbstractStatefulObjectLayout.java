package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractStatefulObjectLayout
extends StatefulObjectLayout {

	public AbstractStatefulObjectLayout(String objectId) {
		super(objectId);
	}
	
	protected abstract int getFieldCount();
	protected abstract StateField getField(int fieldIndex);

	@Override
	public void read(
			StatefulObject object,
			DataInput input,
			IOContext context
	) throws IOException {
		
		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {
			
			StateField field = getField(i);
			if (context == IOContext.COMMS && field.isLocal()) continue;
			field.read(object, input, context);
			
		}
	}

	@Override
	public void write(
			StatefulObject object,
			DataOutput output,
			IOContext context
	) throws IOException {
		
		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {
			
			StateField field = getField(i);
			if (context == IOContext.COMMS && field.isLocal()) continue;
			field.write(object, output, context);
			
		}
	}

	@Override
	public void copy(StatefulObject from, StatefulObject to) {
		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {
			getField(i).copy(from, to);
		}
	}

	@Override
	public int computeHashCode(StatefulObject object) {
		int result = 1;
		
		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {
			
			result = 31 * result + getField(i).computeHashCode(object);
			
		}
		
		return result;
	}

	@Override
	public boolean areEqual(StatefulObject a, StatefulObject b) {
		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {
			if (!getField(i).areEqual(a, b)) return false;
		}
		
		return true;
	}

}
