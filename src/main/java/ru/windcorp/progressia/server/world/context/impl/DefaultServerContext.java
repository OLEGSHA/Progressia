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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.AbstractContextRO;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.WorldLogicRO;
import ru.windcorp.progressia.server.world.context.*;

/**
 * An implementation of the entire {@link ServerContext} tree. The objects of
 * this class are designed to be highly reusable, including reusability in
 * {@linkplain Context#subcontexting subcontexting}. Use this implementation
 * when a {@link WorldLogic} (or a {@link WorldLogicRO}) instance requires a
 * context wrapper.
 * <p>
 * Use other unutilized instances of {@code ReusableServerContext} or
 * {@link #empty()} static method to acquire a usable instance.
 * <p>
 * {@code ReusableServerContext} asserts that is it {@linkplain #isReal() real}
 * and {@linkplain #isImmediate() immediate}. It creates and provides an
 * independent randomness source. The tick length is consulted with the server.
 * Use wrappers to alter these properties.
 * <p>
 * This class defines the outward-facing safe interface of the actual
 * implementation located in {@link DefaultServerContextImpl}. The reasoning
 * for creating a subclass is to allow a single instance to implement both
 * {@linkplain DefaultServerContextBuilders builder interfaces} and the context
 * interface without causing confusion around object states.
 * 
 * @author javapony
 */
public abstract class DefaultServerContext extends AbstractContextRO<BlockData, TileData, EntityData>
	implements ServerTileContext {

	/**
	 * An RSC can conform to a variety of different {@link Context} interfaces.
	 * Each compliance mode is identified by a Role.
	 */
	public enum Role {
		/**
		 * This object has not been configured yet.
		 */
		NONE,
		/**
		 * This object conforms to {@link ServerWorldContext} or
		 * {@link ServerWorldContextRO}.
		 */
		WORLD,
		/**
		 * This object conforms to {@link ServerBlockContext} or
		 * {@link ServerBlockContextRO}.
		 */
		LOCATION,
		/**
		 * This object conforms to {@link ServerTileStackContext} or
		 * {@link ServerTileStackContextRO}.
		 */
		TILE_STACK,
		/**
		 * This object conforms to {@link ServerTileContext} or
		 * {@link ServerTileContextRO}.
		 */
		TILE
	}

	/**
	 * Do not extend ReusableServerContext directly. Use
	 * {@link DefaultServerContextImpl} if this is truly necessary.
	 */
	DefaultServerContext() {
		// do nothing
	}

	/**
	 * Resets this object to its uninitialized state and returns a builder to
	 * reinitialize it.
	 * 
	 * @return a {@link DefaultServerContextBuilders.Empty} instance that may
	 *         be used to reinitialize this object
	 * @throws IllegalStateException if active subcontexting is detected
	 */
	public abstract DefaultServerContextBuilders.Empty reuse() throws IllegalStateException;

	/**
	 * Returns the {@link Role} currently assumed by this object.
	 * 
	 * @return the role
	 */
	public abstract Role getRole();

	/**
	 * Instantiates a new {@link DefaultServerContext} using an appropriate
	 * implementation.
	 * 
	 * @return a {@link DefaultServerContextBuilders.Empty} instance that can
	 *         be used to initialize this object
	 */
	public static DefaultServerContextBuilders.Empty empty() {
		return new DefaultServerContextImpl();
	}
	
	@Override
	public DefaultServerContext push(Vec3i location) {
		super.push(location);
		return this;
	}
	
	@Override
	public DefaultServerContext push(Vec3i location, RelFace face) {
		super.push(location, face);
		return this;
	}
	
	@Override
	public DefaultServerContext push(Vec3i location, RelFace face, int layer) {
		super.push(location, face, layer);
		return this;
	}

}
