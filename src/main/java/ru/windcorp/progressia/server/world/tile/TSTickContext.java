package ru.windcorp.progressia.server.world.tile;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.TickContextMutable;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public interface TSTickContext extends BlockTickContext {
	
	/*
	 * Specifications
	 */
	
	BlockFace getFace();
	
	/*
	 * Getters
	 */
	
	default TileLogicStack getTLSOrNull() {
		ChunkLogic chunkLogic = getChunkLogic();
		if (chunkLogic == null) return null;
		
		return chunkLogic.getTilesOrNull(getBlockInWorld(), getFace());
	}
	
	default TileLogicStack getTLS() {
		return getChunkLogic().getTiles(getBlockInWorld(), getFace());
	}
	
	default TileDataStack getTDSOrNull() {
		ChunkData chunkData = getChunkData();
		if (chunkData == null) return null;
		
		return chunkData.getTilesOrNull(getBlockInWorld(), getFace());
	}
	
	default TileDataStack getTDS() {
		return getChunkData().getTiles(getBlockInWorld(), getFace());
	}
	
	/*
	 * Contexts
	 */
	
	default TileTickContext forLayer(int layer) {
		return TickContextMutable.start().withServer(getServer()).withBlock(getBlockInWorld()).withFace(getFace()).withLayer(layer);
	}
	
	default boolean forEachTile(Consumer<TileTickContext> action) {
		TickContextMutable context = TickContextMutable.uninitialized();
		
		TileDataStack stack = getTDSOrNull();
		if (stack == null || stack.isEmpty()) return false;
		
		for (int layer = 0; layer < stack.size(); ++layer) {
			context.rebuild().withServer(getServer()).withBlock(getBlockInWorld()).withFace(getFace()).withLayer(layer);
			action.accept(context);
		}
		
		return true;
	}
	
	default TSTickContext getComplementary() {
		return TickContextMutable.copyWorld(this)
				.withBlock(getBlockInWorld().add_(getFace().getVector()))
				.withFace(getFace().getCounter())
				.build();
	}
	
	default <R> R evalComplementary(Function<TSTickContext, R> action) {
		Objects.requireNonNull(action, "action");
		return action.apply(getComplementary());
	}
	
	default void forComplementary(Consumer<TSTickContext> action) {
		Objects.requireNonNull(action, "action");
		evalComplementary((Function<TSTickContext, Void>) ctxt -> {
			action.accept(ctxt);
			return null;
		});
	}

}
