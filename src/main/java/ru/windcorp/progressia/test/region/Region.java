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
package ru.windcorp.progressia.test.region;

import static ru.windcorp.progressia.test.region.TestWorldDiskIO.REGION_DIAMETER;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

	// 1 MiB
	private static final int MAX_CHUNK_SIZE = 1024 * 1024;
	private static final int SECTOR_SIZE = MAX_CHUNK_SIZE / 256;
	
	private static final int DEFINITION_SIZE = Integer.BYTES + 1;

	private static final int HEADER_SIZE = DEFINITION_SIZE * REGION_DIAMETER * REGION_DIAMETER * REGION_DIAMETER;

	private final RandomAccessFile file;

	private final ChunkMap<Integer> offsets = ChunkMaps.newHashMap();
	private final ChunkMap<Integer> lengths = ChunkMaps.newHashMap();

	public Region(RandomAccessFile file) throws IOException {
		this.file = file;

		try {
			confirmHeaderHealth();
		} catch (IOException e) {

			TestWorldDiskIO.LOG.debug("Uh the file broke");
			if (RESET_CORRUPTED) {
				byte headerBytes[] = new byte[HEADER_SIZE];
				Arrays.fill(headerBytes, (byte) 0);

				try {
					file.write(headerBytes);
				} catch (IOException e1) {
					e.addSuppressed(e1);
					throw e;
				}
			}

		}
	}

	public RandomAccessFile getFile() {
		return file;
	}

	public void close() throws IOException {
		this.file.close();
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

	public int getLength(Vec3i chunkLoc) {
		return lengths.get(chunkLoc);
	}

	public boolean hasLength(Vec3i pos) {
		return lengths.containsKey(pos);
	}

	public void putLength(Vec3i pos, int length) {
		lengths.put(pos, length);
	}

	private void confirmHeaderHealth() throws IOException {

		Set<Integer> used = new HashSet<Integer>();
		final int chunksPerRegion = REGION_DIAMETER * REGION_DIAMETER * REGION_DIAMETER;

		file.seek(0);

		if (file.length() < HEADER_SIZE) {
			throw new IOException("File is too short to contain a header");
		}

		for (int i = 0; i < chunksPerRegion; i++) {
			int offset = file.readInt();

			int sectorLength = file.read();
			if (sectorLength == 0) {
				continue;
			}

			Vec3i pos = new Vec3i();
			pos.x = i / REGION_DIAMETER / REGION_DIAMETER;
			pos.y = (i / REGION_DIAMETER) % REGION_DIAMETER;
			pos.z = i % REGION_DIAMETER;

			offsets.put(pos, offset);
			lengths.put(pos, sectorLength);

			for (int sector = 0; sector < sectorLength; sector++) {
				if (!used.add(offset + sector)) {
					throw new IOException("A sector is used twice");
				}
			}
		}

	}

	public void save(DefaultChunkData chunk, Server server) throws IOException {
		Vec3i pos = TestWorldDiskIO.getInRegionCoords(chunk.getPosition());
		int definitionOffset = DEFINITION_SIZE * (pos.z + REGION_DIAMETER * (pos.y + REGION_DIAMETER * pos.x));

		if (!hasOffset(pos)) {
			allocateChunk(definitionOffset, pos);
		}
		int dataOffset = getOffset(pos);

		byte[] buffer = saveToBuffer(chunk, server);
		writeBuffer(buffer, definitionOffset, dataOffset, pos);
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
			TestWorldDiskIO.writeGenerationHint(chunk, dataStream, server);
		}

		return arrayStream.toByteArray();
	}

	private void writeBuffer(byte[] buffer, int definitionOffset, int dataOffset, Vec3i pos) throws IOException {
		file.seek(HEADER_SIZE + SECTOR_SIZE * dataOffset);
		file.write(buffer);

		file.seek(definitionOffset + Integer.BYTES);

		int sectors = buffer.length / SECTOR_SIZE + 1;
		file.write(sectors);

		putLength(pos, sectors);
	}

	private void allocateChunk(int definitionOffset, Vec3i pos) throws IOException {
		int outputLen = (int) file.length();

		int dataOffset = (int) (outputLen - HEADER_SIZE) / SECTOR_SIZE + 1;

		file.seek(definitionOffset);
		file.writeInt(dataOffset);

		file.setLength(HEADER_SIZE + dataOffset * SECTOR_SIZE);
		putOffset(pos, dataOffset);
	}

	public DefaultChunkData load(Vec3i chunkPos, DefaultWorldData world, Server server)
		throws IOException,
		DecodingException {

		int dataOffset = 0;
		int sectorLength = 0;
		Vec3i pos = TestWorldDiskIO.getInRegionCoords(chunkPos);

		if (hasOffset(pos)) {
			dataOffset = getOffset(pos);
			sectorLength = getLength(pos);
		} else {
			return null;
		}

		byte[] buffer = readBuffer(dataOffset, sectorLength);
		DefaultChunkData result = loadFromBuffer(buffer, chunkPos, world, server);
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
		TestWorldDiskIO.readGenerationHint(result, dataStream, server);
		return result;
	}

	private byte[] readBuffer(int dataOffset, int sectorLength) throws IOException {
		file.seek(HEADER_SIZE + SECTOR_SIZE * dataOffset);

		byte buffer[] = new byte[SECTOR_SIZE * sectorLength];
		file.read(buffer);
		return buffer;
	}
}