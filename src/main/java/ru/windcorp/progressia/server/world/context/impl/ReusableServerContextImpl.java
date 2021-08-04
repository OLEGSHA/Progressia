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
package ru.windcorp.progressia.server.world.context.impl;

import java.util.Collection;
import java.util.Random;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.TileLogicStack;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.context.ServerTileContext;

class ReusableServerContextImpl extends ReusableServerContext
	implements ReusableServerContextBuilders.Empty, ReusableServerContextBuilders.WithWorld,
	ReusableServerContextBuilders.WithLocation, ReusableServerContextBuilders.WithTileStack {

	/*
	 * STATE MANAGEMENT & UTIL
	 */

	public ReusableServerContextImpl() {
		reuse();
	}

	/**
	 * The relevant {@link Server} instance. If this is {@code null}, the role
	 * is {@link Role#NONE}.
	 */
	protected Server server;

	/**
	 * The relevant {@link WorldLogic} instance. If this is {@code null}, the
	 * role is {@link Role#NONE}.
	 */
	protected WorldLogic world;

	/**
	 * The relevant location. If this is {@code null}, the role is
	 * {@link Role#WORLD} or {@link Role#NONE}.
	 */
	protected Vec3i location;

	/**
	 * A {@code final} reference to the {@link Vec3i} instance used by
	 * {@link #location}.
	 */
	protected final Vec3i locationVectorContainer = new Vec3i();

	/**
	 * The relevant {@link RelFace}. If the role is {@link Role#TILE_STACK} or
	 * {@link Role#TILE}, this is not {@code null}.
	 */
	protected RelFace blockFace;

	/**
	 * The index of the relevant tile. This value is {@code -1} unless the role
	 * is {@link Role#TILE}.
	 */
	protected int index;

	/**
	 * Determines whether this object currently acts as a builder or a context.
	 */
	protected boolean isBuilder;

	/**
	 * Counts the instances of subcontexting that are currently active. This
	 * value increases by 1 when subcontexting begins and decreases by 1 when it
	 * ends. This is always 0 when the object is a builder.
	 */
	protected int subcontextDepth = 0;
	
	/**
	 * The Logic view returned by {@link #logic()}.
	 */
	protected final ReusableServerContextImpl.Logic logic = new Logic();

	/**
	 * Returns the Role currently assumed by this object.
	 * 
	 * @return the role
	 */
	@Override
	public Role getRole() {
		if (server == null)
			return Role.NONE;
		if (location == null)
			return Role.WORLD;
		if (blockFace == null)
			return Role.LOCATION;
		if (index == -1)
			return Role.TILE_STACK;
		return Role.TILE;
	}

	/**
	 * Throws an {@link IllegalStateException} iff this object does not conform
	 * to the specified role. The object must not be a builder to pass the
	 * check.
	 * 
	 * @param role the required role
	 * @return {@code true} (for convenience with {@code assert})
	 * @throws IllegalStateException when the check fails
	 */
	public boolean requireContextRole(Role role) throws IllegalStateException {

		boolean ok = !isBuilder && getRole().compareTo(role) <= 0;
		if (!ok) {
			complainAboutIllegalState(role, false);
		}
		return true;

	}

	/**
	 * Throws an {@link IllegalStateException} iff this object does not conform
	 * to the specified role. The object must be a builder to pass the check. If
	 * {@code role} is {@code null}, any role except {@link Role#NONE} passes.
	 * 
	 * @param role the required role or {@code null}, see above
	 * @return {@code true} (for convenience with {@code assert})
	 * @throws IllegalStateException when the check fails
	 */
	public boolean requireBuilderRole(Role role) {

		boolean ok = isBuilder && role == null ? (getRole() != Role.NONE) : (getRole() == role);
		if (!ok) {
			complainAboutIllegalState(role, true);
		}
		return true;

	}

	private void complainAboutIllegalState(Role role, boolean builder) {
		throw new IllegalStateException(
			"Required " + (builder ? "builder for" : "context") + " " + role
				+ ", but I am currently " + this
		);
	}

	@Override
	public String toString() {
		String result;

		switch (getRole()) {
		case TILE:
			result = String.format(
				"ServerTileContext[x=%d, y=%d, z=%d, %s, index=%d]",
				location.x,
				location.y,
				location.z,
				blockFace,
				index
			);
			break;
		case TILE_STACK:
			result = String
				.format("ServerBlockFaceContext[x=%d, y=%d, z=%d, %s]", location.x, location.y, location.z, blockFace);
			break;
		case LOCATION:
			result = String.format("ServerBlockContext[x=%d, y=%d, z=%d]", location.x, location.y, location.z);
			break;
		case WORLD:
			result = String.format("ServerWorldContext");
			break;
		default:
			result = "Uninitialized ReusableServerContext";
			break;
		}

		if (isBuilder) {
			result = "Builder for " + result;
		}

		return result;
	}

	/*
	 * RSC INTERFACE
	 */

	@Override
	public Empty reuse() {

		if (subcontextDepth != 0) {
			throw new IllegalStateException("Resetting is not allowed when subcontexting");
		}

		server = null;
		world = null;
		location = null;
		blockFace = null;
		index = -1;

		isBuilder = true;

		return this;

	}

	/*
	 * BUILDER INTERFACE
	 */

	@Override
	public ReusableServerContext build() {
		assert requireBuilderRole(null);
		isBuilder = false;
		return this;
	}

	/*
	 * Empty
	 */

	@Override
	public WithWorld in(Server server, WorldLogic world) {
		requireBuilderRole(Role.NONE);
		this.server = server;
		this.world = world;
		return this;
	}

	/*
	 * WithWorld
	 */

	@Override
	public WithLocation at(Vec3i location) {
		requireBuilderRole(Role.WORLD);
		this.location = this.locationVectorContainer;
		this.location.set(location.x, location.y, location.z);
		return this;
	}

	/*
	 * WithLocation
	 */

	@Override
	public WithTileStack on(RelFace side) {
		requireBuilderRole(Role.LOCATION);
		this.blockFace = side;
		return this;
	}
	
	@Override
	public WithTileStack on(BlockFace side) {
		requireBuilderRole(Role.LOCATION);
		this.blockFace = side.relativize(world.getData().getUp(location));
		return this;
	}

	/*
	 * WithTileStack
	 */

	@Override
	public ReusableServerContext index(int index) {
		requireBuilderRole(Role.TILE_STACK);
		this.index = index;
		return build();
	}

	/*
	 * ServerWorldContext.Logic STUFF
	 */
	
	private class Logic implements ServerTileContext.Logic {
		@Override
		public boolean isReal() {
			return ReusableServerContextImpl.this.isReal();
		}

		@Override
		public Collection<? extends ChunkLogic> getChunks() {
			return world.getChunks();
		}

		@Override
		public Collection<EntityData> getEntities() {
			return ReusableServerContextImpl.this.getEntities();
		}

		@Override
		public EntityData getEntity(long entityId) {
			return ReusableServerContextImpl.this.getEntity(entityId);
		}

		@Override
		public Server getServer() {
			return ReusableServerContextImpl.this.getServer();
		}

		@Override
		public Random getRandom() {
			return ReusableServerContextImpl.this.getRandom();
		}

		@Override
		public Vec3i getLocation() {
			return ReusableServerContextImpl.this.getLocation();
		}

		@Override
		public RelFace getFace() {
			return ReusableServerContextImpl.this.getFace();
		}

		@Override
		public int getLayer() {
			return ReusableServerContextImpl.this.getLayer();
		}

		@Override
		public TileLogicStack getTiles(Vec3i blockInWorld, BlockFace face) {
			return world.getTiles(blockInWorld, face);
		}

		@Override
		public ChunkLogic getChunk(Vec3i pos) {
			return world.getChunk(pos);
		}
		
		@Override
		public WorldData getData() {
			return world.getData();
		}

		@Override
		public ServerTileContext data() {
			return ReusableServerContextImpl.this;
		}
		
		@Override
		public String toString() {
			return ReusableServerContextImpl.this + ".Logic";
		}
	}

	@Override
	public Logic logic() {
		return logic;
	}

	/*
	 * LOCATION GETTERS
	 */

	@Override
	public Server getServer() {
		assert requireContextRole(Role.WORLD);
		return server;
	}
	
	@Override
	public Vec3i getLocation() {
		assert requireContextRole(Role.LOCATION);
		return location;
	}
	
	@Override
	public RelFace getFace() {
		assert requireContextRole(Role.TILE_STACK);
		return blockFace;
	}
	
	@Override
	public int getLayer() {
		assert requireContextRole(Role.TILE);
		return index;
	}

	/*
	 * RO CONTEXT INTERFACE
	 */
	
	@Override
	public boolean isReal() {
		assert requireContextRole(Role.WORLD);
		return true;
	}
	
	@Override
	public Random getRandom() {
		assert requireContextRole(Role.WORLD);
		return server.getAdHocRandom();
	}
	
	@Override
	public ChunkData getChunk(Vec3i pos) {
		assert requireContextRole(Role.WORLD);
		return world.getData().getChunk(pos);
	}
	
	@Override
	public Collection<? extends ChunkData> getChunks() {
		assert requireContextRole(Role.WORLD);
		return world.getData().getChunks();
	}
	
	@Override
	public Collection<EntityData> getEntities() {
		assert requireContextRole(Role.WORLD);
		return world.getEntities();
	}
	
	@Override
	public EntityData getEntity(long entityId) {
		assert requireContextRole(Role.WORLD);
		return world.getEntity(entityId);
	}
	
	@Override
	public GravityModel getGravityModel() {
		assert requireContextRole(Role.WORLD);
		return world.getData().getGravityModel();
	}
	
	@Override
	public float getTime() {
		assert requireContextRole(Role.WORLD);
		return world.getData().getTime();
	}

	/*
	 * RO CONTEXT OPTIMIZATIONS
	 */

	/*
	 * RW CONTEXT INTERFACE
	 */
	
	@Override
	public boolean isImmediate() {
		assert requireContextRole(Role.WORLD);
		return true;
	}
	
	@Override
	public void setBlock(Vec3i blockInWorld, BlockData block, boolean notify) {
		assert requireContextRole(Role.WORLD);
		world.getData().setBlock(blockInWorld, block, notify);
	}
	
	@Override
	public void addTile(Vec3i location, BlockFace face, TileData tile) {
		assert requireContextRole(Role.WORLD);
		world.getData().getTiles(location, face).addFarthest(tile);
	}
	
	@Override
	public void removeTile(Vec3i location, BlockFace face, int tag) {
		assert requireContextRole(Role.WORLD);
		TileDataStack stack = world.getData().getTilesOrNull(location, face);
		if (stack == null) return;
		int index = stack.getIndexByTag(tag);
		if (index == -1) return;
		stack.remove(index);
	}
	
	@Override
	public void addEntity(EntityData entity) {
		assert requireContextRole(Role.WORLD);
		world.getData().addEntity(entity);
	}
	
	@Override
	public void removeEntity(long entityId) {
		assert requireContextRole(Role.WORLD);
		world.getData().removeEntity(entityId);
	}
	
	@Override
	public <SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change) {
		assert requireContextRole(Role.WORLD);
		world.getData().changeEntity(entity, change);
	}
	
	@Override
	public void advanceTime(float change) {
		assert requireContextRole(Role.WORLD);
		world.getData().advanceTime(change);
	}

	/*
	 * RW CONTEXT OPTIMIZATIONS
	 */

}
