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

public abstract class AbstractStatefulObjectLayout extends StatefulObjectLayout {

	public AbstractStatefulObjectLayout(String objectId) {
		super(objectId);
	}

	protected abstract int getFieldCount();

	protected abstract StateField getField(int fieldIndex);

	@Override
	public void read(StatefulObject object, DataInput input, IOContext context) throws IOException {

		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {

			StateField field = getField(i);
			if (context == IOContext.COMMS && field.isLocal())
				continue;
			field.read(object, input, context);

		}
	}

	@Override
	public void write(StatefulObject object, DataOutput output, IOContext context) throws IOException {

		int fieldCount = getFieldCount();
		for (int i = 0; i < fieldCount; ++i) {

			StateField field = getField(i);
			if (context == IOContext.COMMS && field.isLocal())
				continue;
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
			if (!getField(i).areEqual(a, b))
				return false;
		}

		return true;
	}

}
