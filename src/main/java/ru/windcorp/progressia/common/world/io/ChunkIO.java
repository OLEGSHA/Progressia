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
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import glm.vec._3.i.Vec3i;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class ChunkIO {

	private static final TByteObjectMap<ChunkCodec> CODECS_BY_ID = new TByteObjectHashMap<>();
	private static final List<ChunkCodec> CODECS_BY_PRIORITY = new ArrayList<>();

	public static ChunkData load(WorldData world, Vec3i position, DataInputStream data, IOContext context)
			throws DecodingException, IOException {
		if (CODECS_BY_ID.isEmpty())
			throw new IllegalStateException("No codecs registered");

		int signature = data.read();
		if (signature < 0)
			throw new EOFException("Expected codec signature, got EOF");

		ChunkCodec codec = getCodec((byte) signature);
		if (codec == null) {
			throw new DecodingException(
					"Unknown codec signature " + Integer.toHexString(signature) + "; is it from the future?");
		}

		try {
			return codec.decode(world, position, data, context);
		} catch (IOException | DecodingException e) {
			throw e;
		} catch (Throwable t) {
			throw CrashReports.report(t, "Codec %s has failed to decode chunk (%d; %d; %d)", codec.getId(), position.x,
					position.y, position.z);
		}
	}

	public static void save(ChunkData chunk, DataOutputStream output, IOContext context) throws IOException {
		ChunkCodec codec = getCodec(chunk, context);

		try {
			output.write(codec.getSignature());
			codec.encode(chunk, output, context);
		} catch (IOException e) {
			throw e;
		} catch (Throwable t) {
			throw CrashReports.report(t, "Codec %s has failed to encode chunk (%d; %d; %d)", codec.getId(),
					chunk.getPosition().x, chunk.getPosition().y, chunk.getPosition().z);
		}
	}

	public static ChunkCodec getCodec(byte signature) {
		if (CODECS_BY_ID.isEmpty())
			throw new IllegalStateException("No codecs registered");
		return CODECS_BY_ID.get(signature);
	}

	public static ChunkCodec getCodec(ChunkData chunk, IOContext context) {
		for (ChunkCodec codec : CODECS_BY_PRIORITY) {
			if (codec.shouldEncode(chunk, context)) {
				return codec;
			}
		}

		if (CODECS_BY_ID.isEmpty())
			throw new IllegalStateException("No codecs registered");
		return CODECS_BY_PRIORITY.get(0);
	}

	/**
	 * Sorted in order of decreasing priority
	 * 
	 * @return
	 */
	public static List<ChunkCodec> getCodecs() {
		return Collections.unmodifiableList(CODECS_BY_PRIORITY);
	}

	public static void registerCodec(ChunkCodec codec) {
		CODECS_BY_PRIORITY.add(0, codec); // Add to the front
		CODECS_BY_ID.put(codec.getSignature(), codec);
	}

	private ChunkIO() {
	}

}
