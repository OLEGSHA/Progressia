/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.common.world;

import java.util.Collection;
import java.util.Collections;

import glm.vec._3.i.Vec3i;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import ru.windcorp.progressia.common.util.CoordinatePacker;

public class WorldData {

	private final TLongObjectMap<ChunkData> chunks = new TLongObjectHashMap<>();
	
	public WorldData() {
		final int size = 1;
		
		for (int x = -(size / 2); x <= (size / 2); ++x) {
			for (int y = -(size / 2); y <= (size / 2); ++y) {
				chunks.put(CoordinatePacker.pack3IntsIntoLong(x, y, 0), new ChunkData(x, y, 0));
			}
		}
	}
	
	public ChunkData getChunk(Vec3i pos) {
		long key = CoordinatePacker.pack3IntsIntoLong(pos.x, pos.y, pos.z);
		return chunks.get(key);
	}
	
	public Collection<ChunkData> getChunks() {
		return Collections.unmodifiableCollection(chunks.valueCollection());
	}
	
}
