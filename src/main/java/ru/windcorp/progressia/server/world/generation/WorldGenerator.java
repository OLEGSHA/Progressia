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
