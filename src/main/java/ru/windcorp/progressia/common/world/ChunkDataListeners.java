package ru.windcorp.progressia.common.world;

import java.util.function.Consumer;
import java.util.function.Supplier;

import glm.vec._3.i.Vec3i;

public class ChunkDataListeners {
	
	public static WorldDataListener createAdder(Supplier<ChunkDataListener> listenerSupplier) {
		return new WorldDataListener() {
			@Override
			public void getChunkListeners(WorldData world, Vec3i chunk, Consumer<ChunkDataListener> chunkListenerSink) {
				chunkListenerSink.accept(listenerSupplier.get());
			}
		};
	}
	
	public static WorldDataListener createAdder(ChunkDataListener listener) {
		return createAdder(() -> listener);
	}
	
	private ChunkDataListeners() {}

}
