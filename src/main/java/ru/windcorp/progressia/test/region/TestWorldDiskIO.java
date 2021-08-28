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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.generic.ChunkMap;
import ru.windcorp.progressia.common.world.generic.ChunkMaps;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.io.WorldContainer;

public class TestWorldDiskIO implements WorldContainer {

	private static final boolean ENABLE = true;

	private static final String FILE_NAME_FORMAT = "region_%d_%d_%d.progressia_region";

	private static final int BITS_IN_CHUNK_COORDS = 4;
	public static final int REGION_DIAMETER = 1 << BITS_IN_CHUNK_COORDS;

	public static Vec3i getRegionCoords(Vec3i chunkCoords) {
		return Coordinates.convertGlobalToCell(BITS_IN_CHUNK_COORDS, chunkCoords, null);
	}

	public static Vec3i getInRegionCoords(Vec3i chunkCoords) {
		return Coordinates.convertGlobalToInCell(BITS_IN_CHUNK_COORDS, chunkCoords, null);
	}

	static final Logger LOG = LogManager.getLogger();

	private final Path path;
	private final ChunkMap<Region> regions = ChunkMaps.newHashMap();

	public TestWorldDiskIO(Path path) {
		this.path = path;
	}

	@Override
	public DefaultChunkData load(Vec3i chunkPos, DefaultWorldData world, Server server) {
		if (!ENABLE) {
			return null;
		}

		try {

			Region region = getRegion(chunkPos, false);
			if (region == null) {
				return null;
			}

			DefaultChunkData result = region.load(chunkPos, world, server);
			return result;

		} catch (IOException | DecodingException e) {
			LOG.warn(
				"Failed to load chunk {} {} {}",
				chunkPos.x,
				chunkPos.y,
				chunkPos.z
			);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void save(DefaultChunkData chunk, DefaultWorldData world, Server server) {
		if (!ENABLE) {
			return;
		}

		try {
			LOG.debug(
				"Saving {} {} {}",
				chunk.getPosition().x,
				chunk.getPosition().y,
				chunk.getPosition().z
			);

			Region region = getRegion(chunk.getPosition(), true);
			region.save(chunk, server);
		} catch (IOException e) {
			LOG.warn(
				"Failed to save chunk {} {} {}",
				chunk.getPosition().x,
				chunk.getPosition().y,
				chunk.getPosition().z
			);
			e.printStackTrace();
		}
	}

	private Region getRegion(Vec3i position, boolean createIfMissing) throws IOException {
		if (regions.isEmpty()) {
			Files.createDirectories(getPath());
		}

		Vec3i regionCoords = getRegionCoords(position);

		Region region = regions.get(regionCoords);
		if (region == null) {

			Path path = getPath().resolve(
				String.format(
					FILE_NAME_FORMAT,
					regionCoords.x,
					regionCoords.y,
					regionCoords.z
				)
			);

			if (!Files.exists(path) && !createIfMissing) {
				return null;
			}

			region = openRegion(path, regionCoords);
		}

		return region;
	}

	private Region openRegion(Path path, Vec3i regionCoords) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
		Region region = new Region(raf);
		regions.put(regionCoords, region);
		return region;
	}

	static void writeGenerationHint(DefaultChunkData chunk, DataOutputStream output, Server server)
		throws IOException {
		server.getWorld().getGenerator().writeGenerationHint(output, chunk.getGenerationHint());
	}

	static void readGenerationHint(DefaultChunkData chunk, DataInputStream input, Server server)
		throws IOException,
		DecodingException {
		chunk.setGenerationHint(server.getWorld().getGenerator().readGenerationHint(input));
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public void close() {
		try {
			for (Region region : regions.values()) {
				region.close();
			}
		} catch (IOException e) {
			CrashReports.report(e, "Could not close region files");
		}
	}

}
