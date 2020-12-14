package ru.windcorp.progressia.server.world;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public interface ChunkTickContext extends TickContext {
	
	Vec3i getChunk();
	
	default ChunkLogic getChunkLogic() {
		return getWorld().getChunk(getChunk());
	}
	
	default ChunkData getChunkData() {
		ChunkLogic chunkLogic = getChunkLogic();
		return chunkLogic == null ? null : chunkLogic.getData();
	}
	
	default void forEachBlock(Consumer<BlockTickContext> action) {
		TickContextMutable context = TickContextMutable.uninitialized();
		
		getChunkData().forEachBlock(blockInChunk -> {
			context.rebuild().withServer(getServer()).withChunk(getChunk()).withBlockInChunk(blockInChunk).build();
			action.accept(context);
		});
	}

}
