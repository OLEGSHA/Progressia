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
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.client.world.tile.TileRenderStack;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.GenericWorld;

public class WorldRender
implements GenericWorld<
	BlockRender,
	TileRender,
	TileRenderStack,
	ChunkRender,
	EntityRenderable
> {
	
	private final WorldData data;
	
	private final Map<ChunkData, ChunkRender> chunks =
			Collections.synchronizedMap(new HashMap<>());
	private final Map<EntityData, EntityRenderable> entityModels =
			Collections.synchronizedMap(new WeakHashMap<>());
	
	public WorldRender(WorldData data) {
		this.data = data;
		
		data.addListener(ChunkDataListeners.createAdder(new ChunkUpdateListener(this)));
		data.addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(WorldData world, ChunkData chunk) {
				chunks.put(chunk, new ChunkRender(WorldRender.this, chunk));
			}
			
			@Override
			public void beforeChunkUnloaded(WorldData world, ChunkData chunk) {
				chunks.remove(chunk);
			}
		});
	}
	
	public WorldData getData() {
		return data;
	}
	
	public ChunkRender getChunk(ChunkData chunkData) {
		return chunks.get(chunkData);
	}
	
	@Override
	public ChunkRender getChunk(Vec3i pos) {
		return chunks.get(getData().getChunk(pos));
	}
	
	@Override
	public Collection<ChunkRender> getChunks() {
		return chunks.values();
	}
	
	@Override
	public Collection<EntityRenderable> getEntities() {
		return entityModels.values();
	}
	
	public void render(ShapeRenderHelper renderer) {
		getChunks().forEach(chunk -> chunk.render(renderer));
		renderEntities(renderer);
	}
	
	private void renderEntities(ShapeRenderHelper renderer) {
		FaceCulling.push(false);
		
		getData().forEachEntity(entity -> {
			renderer.pushTransform().translate(entity.getPosition());
			getEntityRenderable(entity).render(renderer);
			renderer.popTransform();
		});
		
		FaceCulling.pop();
	}

	public EntityRenderable getEntityRenderable(EntityData entity) {
		return entityModels.computeIfAbsent(
				entity,
				WorldRender::createEntityRenderable
		);
	}
	
	private static EntityRenderable createEntityRenderable(EntityData entity) {
		return EntityRenderRegistry.getInstance().get(entity.getId())
				.createRenderable(entity);
	}

}
