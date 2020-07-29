/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.world;

import java.util.ArrayList;
import java.util.Collection;

import ru.windcorp.optica.client.graphics.world.WorldRenderer;
import ru.windcorp.optica.common.world.ChunkData;
import ru.windcorp.optica.common.world.WorldData;

public class WorldRender {
	
	private final WorldData data;
	
	private final Collection<ChunkRender> chunks = new ArrayList<>();
	
	public WorldRender(WorldData data) {
		this.data = data;
		
		for (ChunkData chunkData : data.getChunks()) {
			chunks.add(new ChunkRender(this, chunkData));
		}
	}
	
	public WorldData getData() {
		return data;
	}
	
	public void render(WorldRenderer renderer) {
		renderer.pushWorldTransform().rotateX(-Math.PI / 2);
		
		for (ChunkRender chunk : chunks) {
			chunk.render(renderer);
		}
	}

}
