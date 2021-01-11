package ru.windcorp.progressia.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.Server;

public class TestWorldDiskIO {
	
	private static final Path SAVE_DIR = Paths.get("tmp_world");
	private static final Logger LOG = LogManager.getLogger("TestWorldDiskIO");
	
	private static final boolean ENABLE = false;

	public static void saveChunk(ChunkData chunk, Server server) {
		if (!ENABLE) return;
		
		try {
			LOG.debug(
					"Saving {} {} {}",
					chunk.getPosition().x, chunk.getPosition().y, chunk.getPosition().z
			);
			
			Files.createDirectories(SAVE_DIR);
			
			Path path = SAVE_DIR.resolve(String.format(
					"chunk_%+d_%+d_%+d.progressia_chunk",
					chunk.getPosition().x, chunk.getPosition().y, chunk.getPosition().z
			));
			
			try (DataOutputStream output = new DataOutputStream(new DeflaterOutputStream(new BufferedOutputStream(Files.newOutputStream(path))))) {
				ChunkIO.save(chunk, output, IOContext.SAVE);
				writeGenerationHint(chunk, output, server);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeGenerationHint(ChunkData chunk, DataOutputStream output, Server server) throws IOException {
		server.getWorld().getGenerator().writeGenerationHint(output, chunk.getGenerationHint());
	}

	public static ChunkData tryToLoad(Vec3i chunkPos, WorldData world, Server server) {
		if (!ENABLE) return null;
		
		Path path = SAVE_DIR.resolve(String.format(
				"chunk_%+d_%+d_%+d.progressia_chunk",
				chunkPos.x, chunkPos.y, chunkPos.z
		));
		
		if (!Files.exists(path)) {
			LOG.debug(
					"Not found {} {} {}",
					chunkPos.x, chunkPos.y, chunkPos.z
			);
			
			return null;
		}
		
		try {
			ChunkData result = load(path, chunkPos, world, server);
			
			LOG.debug(
					"Loaded {} {} {}",
					chunkPos.x, chunkPos.y, chunkPos.z
			);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug(
					"Could not load {} {} {}",
					chunkPos.x, chunkPos.y, chunkPos.z
			);
			return null;
		}
	}

	private static ChunkData load(Path path, Vec3i chunkPos, WorldData world, Server server) throws IOException, DecodingException {
		try (DataInputStream input = new DataInputStream(new InflaterInputStream(new BufferedInputStream(Files.newInputStream(path))))) {
			ChunkData chunk = ChunkIO.load(world, chunkPos, input, IOContext.SAVE);
			readGenerationHint(chunk, input, server);
			return chunk;
		}
	}

	private static void readGenerationHint(ChunkData chunk, DataInputStream input, Server server) throws IOException, DecodingException {
		chunk.setGenerationHint(server.getWorld().getGenerator().readGenerationHint(input));
	}

}
