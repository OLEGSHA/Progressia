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

public class IntStateField extends StateField {

	public IntStateField(String id, boolean isLocal, int index) {
		super(id, isLocal, index);
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
	public void read(StatefulObject object, DataInput input, IOContext context) throws IOException {
		object.getStorage().setInt(getIndex(), input.readInt());
	}

	@Override
	public void write(StatefulObject object, DataOutput output, IOContext context) throws IOException {
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
