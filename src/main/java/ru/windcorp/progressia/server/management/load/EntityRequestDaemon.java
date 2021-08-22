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
package ru.windcorp.progressia.server.management.load;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.TLongSet;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.server.Server;

public class EntityRequestDaemon {
	
	private final EntityManager entityManager;
	
	private final TLongCollection buffer = new TLongArrayList();

	public EntityRequestDaemon(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public void tick() {
		synchronized (getServer().getWorld().getData()) {
			synchronized (getServer().getPlayerManager().getMutex()) {
				gatherRequests();
				revokeEntities();
				sendEntities();
			}
		}
	}

	private void gatherRequests() {
		Vec3i v = Vectors.grab3i();
		
		forEachVision(vision -> {
			
			TLongSet requestedEntities = vision.getRequestedEntities();
			requestedEntities.clear();

			ChunkSet visibleChunks = vision.getVisibleChunks();
			getServer().getWorld().forEachEntity(entity -> {
				if (visibleChunks.contains(entity.getChunkCoords(v))) {
					requestedEntities.add(entity.getEntityId());
				}
			});
		});

		Vectors.release(v);
	}

	private void sendEntities() {
		forEachVision(vision -> {
			for (TLongIterator it = vision.getRequestedEntities().iterator(); it.hasNext();) {
				long entityId = it.next();
				if (getEntityManager().isEntityLoaded(entityId) && !vision.getVisibleEntities().contains(entityId)) {
					buffer.add(entityId);
				}
			}
			
			if (buffer.isEmpty()) return;
			for (TLongIterator it = buffer.iterator(); it.hasNext();) {
				getEntityManager().sendEntity(vision.getPlayer(), it.next());
			}
			
			buffer.clear();
		});
	}

	private void revokeEntities() {
		forEachVision(vision -> {
			for (TLongIterator it = vision.getVisibleEntities().iterator(); it.hasNext();) {
				long entityId = it.next();
				if (!getEntityManager().isEntityLoaded(entityId) || !vision.getRequestedEntities().contains(entityId)) {
					buffer.add(entityId);
				}
			}
			
			if (buffer.isEmpty()) return;
			for (TLongIterator it = buffer.iterator(); it.hasNext();) {
				getEntityManager().revokeEntity(vision.getPlayer(), it.next());
			}
			
			buffer.clear();
		});
	}
	
	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public Server getServer() {
		return getEntityManager().getServer();
	}
	
	private void forEachVision(Consumer<? super PlayerVision> action) {
		getEntityManager().getLoadManager().getVisionManager().forEachVision(action);
	}

}
