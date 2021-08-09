/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package ru.windcorp.progressia.server.world;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.TileDataReference;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.context.ServerBlockContextRO;
import ru.windcorp.progressia.server.world.context.ServerTileContextRO;
import ru.windcorp.progressia.server.world.context.ServerWorldContextRO;
import ru.windcorp.progressia.server.world.tasks.TickChunk;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;

public class DefaultChunkLogic implements ChunkLogic {

	private final DefaultWorldLogic world;
	private final DefaultChunkData data;

	private final Collection<Vec3i> tickingBlocks = new ArrayList<>();
	private final Collection<TileDataReference> tickingTiles = new ArrayList<>();

	private final TickChunk tickTask = new TickChunk(this);

	private final Map<TileDataStack, TileLogicStackImpl> tileLogicLists = Collections
		.synchronizedMap(new WeakHashMap<>());

	public DefaultChunkLogic(DefaultWorldLogic world, DefaultChunkData data) {
		this.world = world;
		this.data = data;

		tmp_generateTickLists();
	}

	@Override
	public Vec3i getPosition() {
		return getData().getPosition();
	}
	
	@Override
	public AbsFace getUp() {
		return getData().getUp();
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

	public DefaultWorldLogic getWorld() {
		return world;
	}

	@Override
	public DefaultChunkData getData() {
		return data;
	}

	@Override
	public boolean isReady() {
		return getWorld().getGenerator().isChunkReady(getData().getGenerationHint());
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

	public void forEachTickingTile(BiConsumer<TileDataReference, TileLogic> action) {
		tickingTiles.forEach(ref -> {
			action.accept(
				ref,
				TileLogicRegistry.getInstance().get(ref.get().getId())
			);
		});
	}

	public TickChunk getTickTask() {
		return tickTask;
	}

	private class TileLogicStackImpl extends AbstractList<TileLogic> implements TileLogicStack {
		private class TileLogicReferenceImpl implements TileLogicReference {
			private final TileDataReference parent;

			public TileLogicReferenceImpl (TileDataReference parent) {
				this.parent = parent;
			}
			
			@Override
			public TileDataReference getDataReference() {
				return parent;
			}

			@Override
			public TileLogic get() {
				return TileLogicRegistry.getInstance().get(parent.get().getId());
			}

			@Override
			public int getIndex() {
				return parent.getIndex();
			}

			@Override
			public TileLogicStack getStack() {
				return TileLogicStackImpl.this;
			}
		}
		
		private final TileDataStack parent;

		public TileLogicStackImpl(TileDataStack parent) {
			this.parent = parent;
		}

		@Override
		public Vec3i getBlockInChunk(Vec3i output) {
			return parent.getBlockInChunk(output);
		}

		@Override
		public DefaultChunkLogic getChunk() {
			return DefaultChunkLogic.this;
		}

		@Override
		public RelFace getFace() {
			return parent.getFace();
		}
		
		@Override
		public TileLogicReference getReference(int index) {
			return new TileLogicReferenceImpl(parent.getReference(index));
		}

		@Override
		public int getIndexByTag(int tag) {
			return parent.getIndexByTag(tag);
		}

		@Override
		public int getTagByIndex(int index) {
			return parent.getTagByIndex(index);
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
		ServerWorldContextRO context = Server.getCurrentServer().createContext();
		Vec3i blockInChunk = new Vec3i();
		
		forEachBiW(location -> {
			
			ServerBlockContextRO blockContext = context.push(location);
			
			BlockLogic block = blockContext.logic().getBlock();
			Coordinates.convertInWorldToInChunk(location, blockInChunk);

			if (!(block instanceof TickableBlock))
				return;

			if (((TickableBlock) block).getTickingPolicy(blockContext) == TickingPolicy.REGULAR) {
				tickingBlocks.add(blockInChunk);
			}

			for (RelFace face : RelFace.getFaces()) {
				TileLogicStack stack = getTilesOrNull(blockInChunk, face);
				if (stack == null || stack.isEmpty()) continue;
				
				for (int i = 0; i < stack.size(); ++i) {
					ServerTileContextRO tileContext = blockContext.push(face, i);
					
					TileLogic tile = stack.get(i);

					if (!(tile instanceof TickableTile))
						return;

					if (((TickableTile) tile).getTickingPolicy(tileContext) == TickingPolicy.REGULAR) {
						tickingTiles.add(stack.getData().getReference(i));
					}
					
					tileContext.pop();
				}
			}
			
			blockContext.pop();
			
		});
	}

}
