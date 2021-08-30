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

import glm.vec._3.i.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;
import ru.windcorp.progressia.common.world.generic.ChunkMap;
import ru.windcorp.progressia.common.world.generic.ChunkMaps;
import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.ClientPlayer;
import ru.windcorp.progressia.server.world.io.WorldContainer;
import ru.windcorp.progressia.test.TestContent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestWorldDiskIO implements WorldContainer {

	private static final boolean ENABLE = true;

	private static final String REGION_FOLDER_NAME = "regions";
	private static final String PLAYERS_FOLDER_NAME = "players";
	private static final String REGION_NAME_FORMAT = REGION_FOLDER_NAME + "/" + "region_%d_%d_%d.progressia_region";
	private static final String PLAYER_NAME_FORMAT = PLAYERS_FOLDER_NAME + "/" + "%s.progressia_player";

	private static final int BITS_IN_CHUNK_COORDS = 4;
	public static final int REGION_DIAMETER = 1 << BITS_IN_CHUNK_COORDS;

	public static Vec3i getRegionCoords(Vec3i chunkCoords) {
		return Coordinates.convertGlobalToCell(BITS_IN_CHUNK_COORDS, chunkCoords, null);
	}

	public static Vec3i getInRegionCoords(Vec3i chunkCoords) {
		return Coordinates.convertGlobalToInCell(BITS_IN_CHUNK_COORDS, chunkCoords, null);
	}

	static final Logger LOG = LogManager.getLogger("TestWorldDiskIO");

	private final Path path;
	private final ChunkMap<Region> regions = ChunkMaps.newHashMap();

	public TestWorldDiskIO(Path path) throws IOException {
		this.path = path;

		Files.createDirectories(getPath());
		Files.createDirectories(getPath().resolve(REGION_FOLDER_NAME));
		Files.createDirectories(getPath().resolve(PLAYERS_FOLDER_NAME));
	}

	@Override
	public DefaultChunkData load(Vec3i chunkPos, DefaultWorldData world, Server server) {
		if (!ENABLE) {
			return null;
		}

		try {

			Region region = getRegion(chunkPos, false);
			if (region == null) {
				debug("Could not load chunk {} {} {}: region did not load", chunkPos);
				return null;
			}

			DefaultChunkData result = region.load(chunkPos, world, server);
			return result;

		} catch (IOException | DecodingException e) {
			warn("Failed to load chunk {} {} {}", chunkPos);
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
			debug("Saving chunk {} {} {}", chunk.getPosition());
			Region region = getRegion(chunk.getPosition(), true);
			region.save(chunk, server);
		} catch (IOException e) {
			warn("Failed to save chunk {} {} {}", chunk.getPosition());
			e.printStackTrace();
		}
	}

	@Override
	public Player loadPlayer(String login, ClientPlayer clientPlayer, Server server) {

		Path path = getPlayerPath(login);
		if (!Files.exists(path)) {
			LOG.debug("Could not load player {} because file {} does not exist", login, path);
			return null;
		}

		EntityData player = EntityDataRegistry.getInstance().create("Test:Player");
		try (
			DataInputStream dataInputStream = new DataInputStream(
				new BufferedInputStream(
					Files.newInputStream(
						getPlayerPath(login)
					)
				)
			)
		) {
			player.read(dataInputStream, IOContext.SAVE);
			player.setEntityId(TestContent.PLAYER_ENTITY_ID);
			return new Player(player, server, clientPlayer);
		} catch (IOException ioException) {
			throw CrashReports.report(ioException, "Could not load player data: " + login);
		}
	}

	@Override
	public void savePlayer(Player player, Server server) {
		Path path = getPlayerPath(player.getLogin());
		try (
			DataOutputStream dataOutputStream = new DataOutputStream(
				new BufferedOutputStream(
					Files.newOutputStream(path)
				)
			)
		) {
			player.getEntity().

				write(dataOutputStream, IOContext.SAVE);
		} catch (IOException ioException) {
			throw CrashReports.report(ioException, "Could not save player %s data in file ", player.getLogin(), path);
		}
	}

	private Region getRegion(Vec3i position, boolean createIfMissing) throws IOException {

		Vec3i regionCoords = getRegionCoords(position);

		Region region = regions.get(regionCoords);
		if (region == null) {
			debug("Region {} {} {} is not loaded, loading", regionCoords);

			Path path = getRegionPath(regionCoords);

			if (!createIfMissing && !Files.exists(path)) {
				debug("Region {} {} {} does not exist on disk, aborting load", regionCoords);
				return null;
			}

			region = openRegion(path, regionCoords);
			debug("Region {} {} {} loaded", regionCoords);
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

	private Path getRegionPath(Vec3i regionPos) {
		return getPath().resolve(
			String.format(
				REGION_NAME_FORMAT,
				regionPos.x,
				regionPos.y,
				regionPos.z
			)
		);
	}

	private Path getPlayerPath(String login) {
		return getPath().resolve(
			String.format(
				PLAYER_NAME_FORMAT,
				login
			)
		);
	}

	@Override
	public void close() {
		try {
			for (Region region : regions.values()) {
				region.close();
			}
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not close region files");
		}
	}

	private static void debug(String message, Vec3i vector) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(message, vector.x, vector.y, vector.z);
		}
	}

	private static void warn(String message, Vec3i vector) {
		LOG.warn(message, vector.x, vector.y, vector.z);
	}

}
