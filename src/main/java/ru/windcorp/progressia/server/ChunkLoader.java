package ru.windcorp.progressia.server;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;

public interface ChunkLoader {
	
	void requestChunksToLoad(Consumer<Vec3i> output);

}
