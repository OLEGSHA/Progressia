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

import ru.windcorp.progressia.server.Server;

public class LoadManager {
	
	private final Server server;
	private final ChunkManager chunkManager;
	private final EntityManager entityManager;
	private final VisionManager visionManager;
	
	public LoadManager(Server server) {
		this.server = server;
		
		this.chunkManager = new ChunkManager(this);
		this.entityManager = new EntityManager(this);
		this.visionManager = new VisionManager(this);
	}
	
	/**
	 * @return the chunkManager
	 */
	public ChunkManager getChunkManager() {
		return chunkManager;
	}
	
	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * @return the visionManager
	 */
	public VisionManager getVisionManager() {
		return visionManager;
	}
	
	/**
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}
	
}
