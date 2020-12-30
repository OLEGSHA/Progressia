package ru.windcorp.progressia.common.world;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.entity.EntityData;

public interface WorldDataListener {
	
	/**
	 * Invoked when a new {@link ChunkData} instance is created. This method should be used to add
	 * {@link ChunkDataListener}s to a new chunk. When listeners are added with this method,
	 * their {@link ChunkDataListener#onChunkLoaded(ChunkData) onChunkLoaded} methods will be invoked.
	 * @param world the world instance
	 * @param chunk the {@linkplain Coordinates#chunk coordinates of chunk} of the chunk about to load
	 * @param chunkListenerSink a sink for listeners. All listeners passed to its
	 * {@link Consumer#accept(Object) accept} method will be added to the chunk.
	 */
	default void getChunkListeners(WorldData world, Vec3i chunk, Consumer<ChunkDataListener> chunkListenerSink) {}
	
	/**
	 * Invoked whenever a {@link Chunk} has been loaded.
	 * @param world the world instance
	 * @param chunk the chunk that has loaded
	 */
	default void onChunkLoaded(WorldData world, ChunkData chunk) {}
	
	/**
	 * Invoked whenever a {@link Chunk} is about to be unloaded.
	 * @param world the world instance
	 * @param chunk the chunk that is going to be unloaded
	 */
	default void beforeChunkUnloaded(WorldData world, ChunkData chunk) {}
	
	/**
	 * Invoked whenever an {@link EntityData} has been added.
	 * @param world the world instance
	 * @param entity the entity that has been added
	 */
	default void onEntityAdded(WorldData world, EntityData entity) {}
	
	/**
	 * Invoked whenever an {@link EntityData} is about to be removed.
	 * @param world the world instance
	 * @param entity the entity that is going to be removed
	 */
	default void beforeEntityRemoved(WorldData world, EntityData entity) {}

}
