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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.backend.FaceCulling;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class WorldRender {
	
	private final WorldData data;
	
	private final Map<ChunkData, ChunkRender> chunks = new HashMap<>();
	private final Map<EntityData, Renderable> entityModels =
			Collections.synchronizedMap(new WeakHashMap<>());
	
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
	
	public ChunkRender getChunk(Vec3i pos) {
		return chunks.get(getData().getChunk(pos));
	}
	
	public Collection<ChunkRender> getChunks() {
		return chunks.values();
	}
	
	public void render(ShapeRenderHelper renderer) {
		for (ChunkRender chunk : getChunks()) {
			chunk.render(renderer);
		}
		
		renderEntities(renderer);
	}
	
	private void renderEntities(ShapeRenderHelper renderer) {
		FaceCulling.push(false);
		
		for (ChunkRender chunk : getChunks()) {
			chunk.getData().forEachEntity(entity -> {
					renderer.pushTransform().translate(entity.getPosition());
					getEntityRenderable(entity).render(renderer);
					renderer.popTransform();
			});
		}
		
		FaceCulling.pop();
	}

	public Renderable getEntityRenderable(EntityData entity) {
		return entityModels.computeIfAbsent(
				entity,
				WorldRender::createEntityRenderable
		);
	}
	
	private static Renderable createEntityRenderable(EntityData entity) {
		return EntityRenderRegistry.getInstance().get(entity.getId())
				.createRenderable(entity);
	}

}
