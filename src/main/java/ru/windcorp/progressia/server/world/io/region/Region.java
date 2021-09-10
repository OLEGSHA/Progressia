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
package ru.windcorp.progressia.server.world.io.region;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.generic.ChunkMap;
import ru.windcorp.progressia.common.world.generic.ChunkMaps;
import ru.windcorp.progressia.common.world.io.ChunkIO;
import ru.windcorp.progressia.server.Server;

public class Region {

	private static final boolean RESET_CORRUPTED = true;

	public int loadedChunks;

	private AtomicBoolean isUsing = new AtomicBoolean(false);
	private AtomicBoolean isClosed = new AtomicBoolean(false);

	private final RegionFile file;

	private final ChunkMap<Integer> offsets = ChunkMaps.newHashMap();

	public Region(RandomAccessFile file) throws IOException {
		this.file = new RegionFile(file);

		try {
			this.file.confirmHeaderHealth(offsets);
		} catch (IOException e) {

			RegionWorldContainer.LOG.debug("Uh the file broke");
			if (RESET_CORRUPTED) {
				this.file.makeHeader();
			}

		}
	}

	public RegionFile getFile() {
		return file;
	}

	public void close() throws IOException {
		this.file.close();
		isClosed.lazySet(true);
	}

	public int getOffset(Vec3i chunkLoc) {
		return offsets.get(chunkLoc);
	}

	public boolean hasOffset(Vec3i pos) {
		return offsets.containsKey(pos);
	}

	public void putOffset(Vec3i pos, int offset) {
		offsets.put(pos, offset);
	}

	public AtomicBoolean isClosed() {
		return isClosed;
	}

	public AtomicBoolean isUsing() {
		return isUsing;
	}

	public void save(DefaultChunkData chunk, Server server) throws IOException {
		isUsing.set(true);
		Vec3i pos = RegionWorldContainer.getInRegionCoords(chunk.getPosition());

		if (!hasOffset(pos)) {
			putOffset(pos, file.allocateChunk(pos));
		}
		int dataOffset = getOffset(pos);

		byte[] buffer = saveToBuffer(chunk, server);

		file.writeBuffer(buffer, dataOffset, pos);
		isUsing.set(false);
	}

	private byte[] saveToBuffer(DefaultChunkData chunk, Server server) throws IOException {
		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
		try (
			DataOutputStream dataStream = new DataOutputStream(
				new DeflaterOutputStream(
					new BufferedOutputStream(arrayStream)
				)
			)
		) {
			ChunkIO.save(chunk, dataStream, IOContext.SAVE);
			RegionWorldContainer.writeGenerationHint(chunk, dataStream, server);
		}

		return arrayStream.toByteArray();
	}

	public DefaultChunkData load(Vec3i chunkPos, DefaultWorldData world, Server server)
		throws IOException,
		DecodingException {
		isUsing.set(true);

		int dataOffset = 0;
		Vec3i pos = RegionWorldContainer.getInRegionCoords(chunkPos);

		if (hasOffset(pos)) {
			dataOffset = getOffset(pos);
		} else {
			return null;
		}

		byte[] buffer = file.readBuffer(dataOffset);
		DefaultChunkData result = loadFromBuffer(buffer, chunkPos, world, server);
		isUsing.set(false);
		return result;
	}

	private DefaultChunkData loadFromBuffer(byte[] buffer, Vec3i chunkPos, DefaultWorldData world, Server server)
		throws IOException,
		DecodingException {

		DataInputStream dataStream = new DataInputStream(
			new InflaterInputStream(
				new BufferedInputStream(
					new ByteArrayInputStream(buffer)
				)
			)
		);

		DefaultChunkData result = ChunkIO.load(world, chunkPos, dataStream, IOContext.SAVE);
		RegionWorldContainer.readGenerationHint(result, dataStream, server);
		return result;
	}
}