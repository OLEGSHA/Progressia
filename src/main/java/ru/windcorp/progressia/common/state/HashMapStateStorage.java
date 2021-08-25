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

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class HashMapStateStorage extends StateStorage {

	private final TIntIntMap ints = new TIntIntHashMap();
	private final TIntObjectMap<Object> objects = new TIntObjectHashMap<>();

	@Override
	public int getInt(int index) {
		return ints.get(index);
	}

	@Override
	public void setInt(int index, int value) {
		ints.put(index, value);
	}
	
	@Override
	public Object getObject(int index) {
		return objects.get(index);
	}
	
	@Override
	public void setObject(int index, Object object) {
		objects.put(index, object);
	}

}
