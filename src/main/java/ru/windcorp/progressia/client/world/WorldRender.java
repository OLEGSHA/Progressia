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
package ru.windcorp.progressia.client.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;

public class WorldRender {
	
	private final WorldData data;
	
	private final Map<ChunkData, ChunkRender> chunks = new HashMap<>();
	
	public WorldRender(WorldData data) {
		this.data = data;
		
		for (ChunkData chunkData : data.getChunks()) {
			chunks.put(chunkData, new ChunkRender(this, chunkData));
		}
	}
	
	public WorldData getData() {
		return data;
	}
	
	public ChunkRender getChunk(ChunkData chunkData) {
		return chunks.get(chunkData);
	}
	
	public ChunkRender getChunk(int x, int y, int z) {
		return chunks.get(getData().getChunk(x, y, z));
	}
	
	public Collection<ChunkRender> getChunks() {
		return chunks.values();
	}
	
	public void render(ShapeRenderHelper renderer) {
		for (ChunkRender chunk : getChunks()) {
			chunk.render(renderer);
		}
	}

}
