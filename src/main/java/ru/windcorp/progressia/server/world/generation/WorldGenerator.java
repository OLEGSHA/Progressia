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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public abstract class WorldGenerator extends Namespaced {

	WorldGenerator(String id) {
		super(id);
		// package-private constructor; extend AbstractWorldGeneration
	}

	public abstract ChunkData generate(Vec3i chunkPos, WorldData world);

	public abstract Object readGenerationHint(DataInputStream input) throws IOException, DecodingException;

	public abstract void writeGenerationHint(DataOutputStream output, Object hint) throws IOException;

	public abstract boolean isChunkReady(Object hint);

}
