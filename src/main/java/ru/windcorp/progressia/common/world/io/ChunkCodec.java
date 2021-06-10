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

package ru.windcorp.progressia.common.world.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public abstract class ChunkCodec extends Namespaced {

	private final byte signature;

	public ChunkCodec(String id, byte signature) {
		super(id);
		this.signature = signature;
	}

	public ChunkCodec(String id, int signature) {
		this(id, (byte) signature);
	}

	public byte getSignature() {
		return signature;
	}

	public abstract ChunkData decode(WorldData world, Vec3i position, DataInputStream input, IOContext context)
			throws DecodingException, IOException;

	public abstract boolean shouldEncode(ChunkData chunk, IOContext context);

	public abstract void encode(ChunkData chunk, DataOutputStream output, IOContext context) throws IOException;

}
