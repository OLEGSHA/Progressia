package ru.windcorp.progressia.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.GenericChunk;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.common.world.tile.TileReference;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.entity.EntityLogic;
import ru.windcorp.progressia.server.world.entity.EntityLogicRegistry;
import ru.windcorp.progressia.server.world.tasks.TickChunk;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;
import ru.windcorp.progressia.server.world.tile.TileLogicStack;

public class ChunkLogic implements GenericChunk<
	ChunkLogic,
	BlockLogic,
	TileLogic,
	TileLogicStack
> {
	
	private final WorldLogic world;
	private final ChunkData data;
	
	private final Collection<Vec3i> tickingBlocks = new ArrayList<>();
	private final Collection<TileReference> tickingTiles = new ArrayList<>();
	
	private final TickChunk tickTask = new TickChunk(this);
	
	private final Map<TileDataStack, TileLogicStackImpl> tileLogicLists =
			Collections.synchronizedMap(new WeakHashMap<>());
	
	public ChunkLogic(WorldLogic world, ChunkData data) {
		this.world = world;
		this.data = data;
		
		tmp_generateTickLists();
	}
	
	@Override
	public Vec3i getPosition() {
		return getData().getPosition();
	}
	
	@Override
	public BlockLogic getBlock(Vec3i blockInChunk) {
		return BlockLogicRegistry.getInstance().get(
				getData().getBlock(blockInChunk).getId()
		);
	}
	
	@Override
	public TileLogicStack getTiles(Vec3i blockInChunk, BlockFace face) {
		return getTileStackWrapper(getData().getTiles(blockInChunk, face));
	}
	
	@Override
	public boolean hasTiles(Vec3i blockInChunk, BlockFace face) {
		return getData().hasTiles(blockInChunk, face);
	}
	
	private TileLogicStack getTileStackWrapper(TileDataStack tileDataList) {
		return tileLogicLists.computeIfAbsent(
				tileDataList,
				TileLogicStackImpl::new
		);
	}

	public WorldLogic getWorld() {
		return world;
	}
	
	public ChunkData getData() {
		return data;
	}
	
	public boolean hasTickingBlocks() {
		return !tickingBlocks.isEmpty();
	}
	
	public boolean hasTickingTiles() {
		return !tickingTiles.isEmpty();
	}
	
	public void forEachTickingBlock(BiConsumer<Vec3i, BlockLogic> action) {
		tickingBlocks.forEach(blockInChunk -> {
			action.accept(blockInChunk, getBlock(blockInChunk));
		});
	}
	
	public void forEachTickingTile(BiConsumer<TileReference, TileLogic> action) {
		tickingTiles.forEach(ref -> {
			action.accept(
					ref,
					TileLogicRegistry.getInstance().get(ref.get().getId())
			);
		});
	}
	
	public void forEachEntity(BiConsumer<EntityLogic, EntityData> action) {
		getData().forEachEntity(data -> {
			action.accept(
					EntityLogicRegistry.getInstance().get(data.getId()),
					data
			);
		});
	}
	
	public TickChunk getTickTask() {
		return tickTask;
	}
	
	private class TileLogicStackImpl extends TileLogicStack {
		
		private final TileDataStack parent;

		public TileLogicStackImpl(TileDataStack parent) {
			this.parent = parent;
		}

		@Override
		public Vec3i getBlockInChunk(Vec3i output) {
			return parent.getBlockInChunk(output);
		}

		@Override
		public ChunkLogic getChunk() {
			return ChunkLogic.this;
		}

		@Override
		public BlockFace getFace() {
			return parent.getFace();
		}

		@Override
		public TileLogic get(int index) {
			return TileLogicRegistry.getInstance().get(parent.get(index).getId());
		}

		@Override
		public int size() {
			return parent.size();
		}
		
		@Override
		public TileDataStack getData() {
			return parent;
		}
		
	}
	
	private void tmp_generateTickLists() {
		ChunkTickContext context = TickContextMutable.start().withChunk(this).build();
		
		context.forEachBlock(bctxt -> {
			BlockLogic block = bctxt.getBlock();
			
			if (!(block instanceof TickableBlock)) return;
			
			if (((TickableBlock) block).getTickingPolicy(bctxt) == TickingPolicy.REGULAR) {
				tickingBlocks.add(Coordinates.convertInWorldToInChunk(bctxt.getBlockInWorld(), null));
			}
			
			bctxt.forEachFace(fctxt -> fctxt.forEachTile(tctxt -> {
				TileLogic tile = tctxt.getTile();
				
				if (!(tile instanceof TickableTile)) return;
				
				if (((TickableTile) tile).getTickingPolicy(tctxt) == TickingPolicy.REGULAR) {
					tickingTiles.add(tctxt.getReference());
				}
			}));
		});
	}

}
