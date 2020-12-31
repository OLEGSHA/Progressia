package ru.windcorp.progressia.server.world.generation;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;

public abstract class WorldGenerator extends Namespaced {

	public WorldGenerator(String id) {
		super(id);
	}
	
	public abstract ChunkData generate(Vec3i chunkPos, WorldData world);

}
