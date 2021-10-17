/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BooleanStateField extends StateField {

	public BooleanStateField(
		String id,
		boolean isLocal,
		int index
	) {
		super(id, isLocal, index);
	}

	public boolean get(StatefulObject object) {
		return object.getStorage().getBoolean(getIndex());
	}

	public void setNow(StatefulObject object, boolean value) {
		object.getStorage().setBoolean(getIndex(), value);
	}

	public void set(StateChanger changer, boolean value) {
		changer.setBoolean(this, value);
	}

	@Override
	public void read(
		StatefulObject object,
		DataInput input,
		IOContext context
	)
		throws IOException {
		object.getStorage().setBoolean(getIndex(), input.readBoolean());
	}

	@Override
	public void write(
		StatefulObject object,
		DataOutput output,
		IOContext context
	)
		throws IOException {
		output.writeBoolean(object.getStorage().getBoolean(getIndex()));
	}

	@Override
	public void copy(StatefulObject from, StatefulObject to) {
		setNow(to, get(from));
	}

	@Override
	public int computeHashCode(StatefulObject object) {
		return Boolean.hashCode(get(object));
	}

	@Override
	public boolean areEqual(StatefulObject a, StatefulObject b) {
		return get(a) == get(b);
	}
	
	@Override
	public void setDefault(StateStorage storage) {
		storage.setBoolean(getIndex(), false);
	}

}
