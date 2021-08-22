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

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.DefaultWorldLogic;

public abstract class WorldGenerator extends Namespaced {

	WorldGenerator(String id) {
		super(id);
		// package-private constructor; extend AbstractWorldGeneration
	}

	public abstract DefaultChunkData generate(Vec3i chunkPos);

	public abstract Object readGenerationHint(DataInputStream input) throws IOException, DecodingException;

	public abstract void writeGenerationHint(DataOutputStream output, Object hint) throws IOException;

	public abstract boolean isChunkReady(Object hint);
	
	public abstract GravityModel getGravityModel();
	
	public abstract Vec3 suggestSpawnLocation();
	
	public abstract Server getServer();
	public abstract DefaultWorldLogic getWorldLogic();
	public abstract DefaultWorldData getWorldData();

}
