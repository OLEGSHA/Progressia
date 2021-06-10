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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericTileStack;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.common.world.tile.TileReference;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.tile.TSTickContext;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public abstract class TickContextMutable implements BlockTickContext, TSTickContext, TileTickContext {

	private static enum Role {
		NONE, WORLD, CHUNK, BLOCK, TILE_STACK, TILE;
	}

	/*
	 * TickContextMutable interface
	 */

	// Only TickContextMutable.Impl can extend; extend Impl if need be
	private TickContextMutable() {
	}

	public abstract Builder.Empty rebuild();

	/*
	 * Static methods
	 */

	public static TickContextMutable uninitialized() {
		return new Impl();
	}

	public static Builder.Empty start() {
		return uninitialized().rebuild();
	}

	public static Builder.World copyWorld(TickContext context) {
		return start().withServer(context.getServer());
	}

	public static Builder.Chunk copyChunk(ChunkTickContext context) {
		return start().withChunk(context.getChunkLogic());
	}

	public static Builder.Block copyBlock(BlockTickContext context) {
		return copyWorld(context).withBlock(context.getBlockInWorld());
	}

	public static Builder.TileStack copyTS(TSTickContext context) {
		return copyBlock(context).withFace(context.getFace());
	}

	public static TileTickContext copyTile(TileTickContext context) {
		return copyTS(context).withLayer(context.getLayer());
	}

	/*
	 * Builder interfaces
	 */

	public static interface Builder {
		TickContextMutable build();

		public static interface Empty /* does not extend Builder */ {
			World withServer(Server server);

			default Builder.World withWorld(WorldLogic world) {
				Objects.requireNonNull(world, "world");
				return withServer(world.getServer());
			}

			default Builder.Chunk withChunk(ChunkLogic chunk) {
				Objects.requireNonNull(chunk, "chunk");
				return withWorld(chunk.getWorld()).withChunk(chunk.getPosition());
			}
		}

		public static interface World extends Builder {
			Chunk withChunk(Vec3i chunk);

			Block withBlock(Vec3i blockInWorld);

			TileStack withTS(GenericTileStack<?, ?, ?> tileStack);

			default Builder.Chunk withChunk(ChunkData chunk) {
				Objects.requireNonNull(chunk, "chunk");
				return withChunk(chunk.getPosition());
			}

			default TileTickContext withTile(TileReference ref) {
				Objects.requireNonNull(ref, "ref");
				return withTS(ref.getStack()).withLayer(ref.getIndex());
			}
		}

		public static interface Chunk extends Builder {
			Builder.Block withBlockInChunk(Vec3i blockInChunk);
		}

		public static interface Block extends Builder {
			Builder.TileStack withFace(BlockFace face);
		}

		public static interface TileStack extends Builder {
			TickContextMutable withLayer(int layer);
		}
	}

	/*
	 * Impl
	 */

	public static class Impl extends TickContextMutable
			implements Builder.Empty, Builder.World, Builder.Chunk, Builder.Block, Builder.TileStack {

		protected Impl() {
		}

		protected Server server;
		protected final Vec3i chunk = new Vec3i();
		protected final Vec3i blockInWorld = new Vec3i();
		protected BlockFace face;
		protected int layer;

		protected Role role = Role.NONE;
		protected boolean isBeingBuilt = false;

		/**
		 * Updated lazily
		 */
		protected final Vec3i blockInChunk = new Vec3i();

		/*
		 * TickContextMutable
		 */

		@Override
		public Server getServer() {
			checkContextState(Role.WORLD);
			return this.server;
		}

		@Override
		public float getTickLength() {
			checkContextState(Role.WORLD);
			return (float) this.server.getTickLength();
		}

		@Override
		public Vec3i getChunk() {
			checkContextState(Role.CHUNK);
			return this.chunk;
		}

		@Override
		public Vec3i getBlockInWorld() {
			checkContextState(Role.BLOCK);
			return this.blockInWorld;
		}

		@Override
		public BlockFace getFace() {
			checkContextState(Role.TILE_STACK);
			return this.face;
		}

		@Override
		public int getLayer() {
			checkContextState(Role.TILE);
			return this.layer;
		}

		@Override
		public Builder.Empty rebuild() {
			this.role = Role.NONE;
			this.isBeingBuilt = true;

			this.server = null;
			this.chunk.set(0);
			this.blockInWorld.set(0);
			this.face = null;
			this.layer = -1;

			return this;
		}

		/*
		 * Builder memo: do NOT use Context getters, they throw ISEs
		 */

		@Override
		public TickContextMutable build() {
			checkBuilderState(null);
			this.isBeingBuilt = false;
			return this;
		}

		@Override
		public World withServer(Server server) {
			Objects.requireNonNull(server, "server");
			checkBuilderState(Role.NONE);
			this.server = server;
			this.role = Role.WORLD;
			return this;
		}

		@Override
		public Chunk withChunk(Vec3i chunk) {
			Objects.requireNonNull(chunk, "chunk");
			checkBuilderState(Role.WORLD);

			this.chunk.set(chunk.x, chunk.y, chunk.z);

			this.role = Role.CHUNK;
			return this;
		}

		@Override
		public Block withBlock(Vec3i blockInWorld) {
			Objects.requireNonNull(blockInWorld, "blockInWorld");
			checkBuilderState(Role.WORLD);

			this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
			Coordinates.convertInWorldToChunk(blockInWorld, this.chunk);

			this.role = Role.BLOCK;
			return this;
		}

		@Override
		public TileStack withTS(GenericTileStack<?, ?, ?> tileStack) {
			Objects.requireNonNull(tileStack, "tileStack");

			return withBlock(tileStack.getBlockInWorld(this.blockInWorld)).withFace(tileStack.getFace());
			// ^^^^^^^^^^^^^^^^^ This is safe
		}

		@Override
		public Block withBlockInChunk(Vec3i blockInChunk) {
			Objects.requireNonNull(blockInChunk, "blockInChunk");
			checkBuilderState(Role.CHUNK);

			Coordinates.getInWorld(this.chunk, blockInChunk, this.blockInWorld);

			this.role = Role.BLOCK;
			return this;
		}

		@Override
		public TileStack withFace(BlockFace face) {
			Objects.requireNonNull(face, "face");
			checkBuilderState(Role.BLOCK);

			this.face = face;

			this.role = Role.TILE_STACK;
			return this;
		}

		@Override
		public TickContextMutable withLayer(int layer) {
			checkBuilderState(Role.TILE);

			this.layer = layer;

			this.role = Role.TILE;
			return build();
		}

		/*
		 * Optimization
		 */

		@Override
		public Vec3i getBlockInChunk() {
			return Coordinates.convertInWorldToInChunk(getBlockInWorld(), this.blockInChunk);
		}

		@Override
		public void forEachBlock(Consumer<BlockTickContext> action) {
			checkContextState(Role.CHUNK);

			Vec3i v = this.blockInWorld;

			int previousX = v.x;
			int previousY = v.y;
			int previousZ = v.z;
			Role previousRole = this.role;

			this.role = Role.BLOCK;

			final int minX = Coordinates.getInWorld(chunk.x, 0);
			final int minY = Coordinates.getInWorld(chunk.y, 0);
			final int minZ = Coordinates.getInWorld(chunk.z, 0);
			final int size = ChunkData.BLOCKS_PER_CHUNK;

			for (v.x = minX; v.x < minX + size; ++v.x) {
				for (v.y = minY; v.y < minY + size; ++v.y) {
					for (v.z = minZ; v.z < minZ + size; ++v.z) {
						action.accept(this);
					}
				}
			}

			this.role = previousRole;
			blockInWorld.set(previousX, previousY, previousZ);
		}

		@Override
		public void forEachFace(Consumer<TSTickContext> action) {
			checkContextState(Role.BLOCK);
			BlockFace previousFace = this.face;
			Role previousRole = this.role;

			this.role = Role.TILE_STACK;
			for (int i = 0; i < BlockFace.BLOCK_FACE_COUNT; ++i) {
				this.face = BlockFace.getFaces().get(i);
				action.accept(this);
			}

			this.role = previousRole;
			this.face = previousFace;
		}

		@Override
		public <R> R evalNeighbor(Vec3i direction, Function<BlockTickContext, R> action) {
			this.blockInWorld.add(direction);
			R result = action.apply(this);
			this.blockInWorld.sub(direction);

			return result;
		}

		@Override
		public void forNeighbor(Vec3i direction, Consumer<BlockTickContext> action) {
			this.blockInWorld.add(direction);
			action.accept(this);
			this.blockInWorld.sub(direction);
		}

		@Override
		public boolean forEachTile(Consumer<TileTickContext> action) {
			checkContextState(Role.TILE_STACK);
			int previousLayer = this.layer;
			Role previousRole = this.role;

			this.role = Role.TILE;
			TileDataStack stack = getTDSOrNull();
			if (stack == null || stack.isEmpty())
				return false;

			for (this.layer = 0; this.layer < stack.size(); ++this.layer) {
				action.accept(this);
			}

			this.role = previousRole;
			this.layer = previousLayer;
			return true;
		}

		@Override
		public <R> R evalComplementary(Function<TSTickContext, R> action) {
			Objects.requireNonNull(action, "action");
			checkContextState(Role.TILE_STACK);

			this.blockInWorld.add(this.face.getVector());
			this.face = this.face.getCounter();
			R result = action.apply(this);
			this.face = this.face.getCounter();
			this.blockInWorld.sub(this.face.getVector());

			return result;
		}

		@Override
		public void forComplementary(Consumer<TSTickContext> action) {
			Objects.requireNonNull(action, "action");
			checkContextState(Role.TILE_STACK);

			this.blockInWorld.add(this.face.getVector());
			this.face = this.face.getCounter();
			action.accept(this);
			this.face = this.face.getCounter();
			this.blockInWorld.sub(this.face.getVector());
		}

		/*
		 * Misc
		 */

		protected void checkContextState(Role requiredRole) {
			if (isBeingBuilt) {
				throw new IllegalStateException("This context is still being built");
			}

			if ((role == null) || (requiredRole.compareTo(role) > 0)) {
				throw new IllegalStateException(
						"This context is currently initialized as " + role + "; requested " + requiredRole);
			}
		}

		protected void checkBuilderState(Role requiredRole) {
			if (!isBeingBuilt) {
				throw new IllegalStateException("This context is already built");
			}

			if (requiredRole == null) {
				if (role == Role.NONE) {
					throw new IllegalStateException("This context is currently not initialized");
				}
			} else {
				if (role != requiredRole) {
					throw new IllegalStateException(
							"This context is currently initialized as " + role + "; requested " + requiredRole);
				}
			}
		}

		@Override
		public String toString() {
			final String format;

			switch (this.role) {
			case WORLD:
				format = "TickContext";
				break;
			case CHUNK:
				format = "(%2$d; %3$d; %4$d)";
				break;
			case BLOCK:
				format = "(%5$d; %6$d; %7$d)";
				break;
			case TILE_STACK:
				format = "((%5$d; %6$d; %7$d); %8$6s)";
				break;
			case TILE:
				format = "((%5$d; %6$d; %7$d); %8$6s; %9$d)";
				break;
			case NONE:
			default:
				format = "Uninitialized TickContextMutable";
				break;
			}

			return String.format(format, this.chunk.x, this.chunk.y, this.chunk.z, this.blockInWorld.x,
					this.blockInWorld.y, this.blockInWorld.z, this.face, this.layer);
		}
	}

}
