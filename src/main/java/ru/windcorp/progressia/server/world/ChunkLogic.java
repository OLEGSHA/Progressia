package ru.windcorp.progressia.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.server.block.BlockLogic;
import ru.windcorp.progressia.server.block.BlockLogicRegistry;
import ru.windcorp.progressia.server.block.Tickable;

public class ChunkLogic {
	
	private final WorldLogic world;
	private final ChunkData data;
	
	private final Collection<Vec3i> ticking = new ArrayList<>();
	
	public ChunkLogic(WorldLogic world, ChunkData data) {
		this.world = world;
		this.data = data;
		
		generateTickList();
	}
	
	private void generateTickList() {
		MutableBlockTickContext blockTickContext =
				new MutableBlockTickContext();
		
		blockTickContext.setWorld(getWorld());
		blockTickContext.setChunk(this);
		
		data.forEachBlock(blockInChunk -> {
			BlockLogic block = getBlock(blockInChunk);
			
			if (block instanceof Tickable) {
				blockTickContext.setCoordsInChunk(blockInChunk);
				
				if (((Tickable) block).doesTickRegularly(blockTickContext)) {
					ticking.add(new Vec3i(blockInChunk));
				}
			}
		});
	}

	public WorldLogic getWorld() {
		return world;
	}
	
	public ChunkData getData() {
		return data;
	}
	
	public boolean hasTickingBlocks() {
		return ticking.isEmpty();
	}
	
	public void forEachTickingBlock(BiConsumer<Vec3i, BlockLogic> action) {
		ticking.forEach(blockInChunk -> {
			action.accept(blockInChunk, getBlock(blockInChunk));
		});
	}
	
	public BlockLogic getBlock(Vec3i blockInChunk) {
		return BlockLogicRegistry.get(getData().getBlock(blockInChunk).getId());
	}

}
