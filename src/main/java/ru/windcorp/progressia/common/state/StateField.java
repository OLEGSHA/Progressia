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

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

public abstract class StateField extends Namespaced {

	private final boolean isLocal;
	private final int index;

	public StateField(String id, boolean isLocal, int index) {
		super(id);
		this.isLocal = isLocal;
		this.index = index;
	}

	public boolean isLocal() {
		return isLocal;
	}

	protected int getIndex() {
		return index;
	}

	public abstract void read(StatefulObject object, DataInput input, IOContext context) throws IOException;

	public abstract void write(StatefulObject object, DataOutput output, IOContext context) throws IOException;

	public abstract void copy(StatefulObject from, StatefulObject to);

	public abstract int computeHashCode(StatefulObject object);

	public abstract boolean areEqual(StatefulObject a, StatefulObject b);

}
