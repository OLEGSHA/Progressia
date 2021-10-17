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

public class OptimizedStateStorage extends StateStorage {

	private final int[] ints;
	private final boolean[] booleans;
	private final Object[] objects;

	public OptimizedStateStorage(PrimitiveCounters sizes) {
		this.ints = sizes.getInts() == 0 ? null : new int[sizes.getInts()];
		this.booleans = sizes.getBooleans() == 0 ? null : new boolean[sizes.getBooleans()];
		this.objects = sizes.getObjects() == 0 ? null : new Object[sizes.getObjects()];
	}

	@Override
	public int getInt(int index) {
		return ints[index];
	}

	@Override
	public void setInt(int index, int value) {
		ints[index] = value;
	}
	
	@Override
	public boolean getBoolean(int index) {
		return booleans[index];
	}
	
	@Override
	public void setBoolean(int index, boolean value) {
		booleans[index] = value;
	}
	
	@Override
	public Object getObject(int index) {
		return objects[index];
	}
	
	@Override
	public void setObject(int index, Object object) {
		objects[index] = object;
	}

}
