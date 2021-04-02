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
 
package ru.windcorp.progressia.server.world.generation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.GravityModelRegistry;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.WorldLogic;

public abstract class AbstractWorldGenerator<H> extends WorldGenerator {

	private final Class<H> hintClass;
	
	private final GravityModel gravityModel;
	
	private final Server server;

	public AbstractWorldGenerator(String id, Server server, Class<H> hintClass, String gravityModelId) {
		super(id);
		this.hintClass = Objects.requireNonNull(hintClass, "hintClass");
		this.gravityModel = GravityModelRegistry.getInstance().create(Objects.requireNonNull(gravityModelId, "gravityModelId"));
		this.server = server;
		
		if (this.gravityModel == null) {
			throw new IllegalArgumentException("Gravity model with ID \"" + gravityModelId + "\" not found");
		}
	}

	@Override
	public final Object readGenerationHint(DataInputStream input) throws IOException, DecodingException {
		return doReadGenerationHint(input);
	}

	@Override
	public final void writeGenerationHint(DataOutputStream output, Object hint) throws IOException {
		doWriteGenerationHint(output, hintClass.cast(hint));
	}

	protected abstract H doReadGenerationHint(DataInputStream input) throws IOException, DecodingException;

	protected abstract void doWriteGenerationHint(DataOutputStream output, H hint) throws IOException;

	@Override
	public final boolean isChunkReady(Object hint) {
		return checkIsChunkReady(hintClass.cast(hint));
	}

	protected abstract boolean checkIsChunkReady(H hint);

	protected H getHint(ChunkData chunk) {
		return hintClass.cast(chunk.getGenerationHint());
	}

	protected void setHint(ChunkData chunk, H hint) {
		chunk.setGenerationHint(hint);
	}
	
	@Override
	public GravityModel getGravityModel() {
		return gravityModel;
	}
	
	@Override
	public Server getServer() {
		return server;
	}
	
	@Override
	public WorldLogic getWorldLogic() {
		return server.getWorld();
	}
	
	@Override
	public WorldData getWorldData() {
		return server.getWorld().getData();
	}

}
