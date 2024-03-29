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
	private final Object[] objects;

	public OptimizedStateStorage(PrimitiveCounters sizes) {
		this.ints = new int[sizes.getInts()];
		this.objects = new Object[sizes.getObjects()];
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
	public Object getObject(int index) {
		return objects[index];
	}
	
	@Override
	public void setObject(int index, Object object) {
		objects[index] = object;
	}

}
