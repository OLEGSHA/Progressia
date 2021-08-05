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

import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.WorldLogicRO;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerBlockContextRO;
import ru.windcorp.progressia.server.world.context.ServerBlockFaceContext;
import ru.windcorp.progressia.server.world.context.ServerBlockFaceContextRO;
import ru.windcorp.progressia.server.world.context.ServerContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileContextRO;
import ru.windcorp.progressia.server.world.context.ServerWorldContext;
import ru.windcorp.progressia.server.world.context.ServerWorldContextRO;

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
 * implementation located in {@link ReusableServerContextImpl}. The reasoning
 * for creating a subclass is to allow a single instance to implement both
 * {@linkplain ReusableServerContextBuilders builder interfaces} and the context
 * interface without causing confusion around object states.
 * 
 * @author javapony
 */
public abstract class ReusableServerContext implements ServerTileContext {

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
		 * This object conforms to {@link ServerBlockFaceContext} or
		 * {@link ServerBlockFaceContextRO}.
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
	 * {@link ReusableServerContextImpl} if this is truly necessary.
	 */
	ReusableServerContext() {
		// do nothing
	}

	/**
	 * Resets this object to its uninitialized state and returns a builder to
	 * reinitialize it.
	 * 
	 * @return a {@link ReusableServerContextBuilders.Empty} instance that may
	 *         be used to reinitialize this object
	 * @throws IllegalStateException if active subcontexting is detected.
	 *                               Detection is done on a best-effort basis;
	 *                               do not rely this exception
	 */
	public abstract ReusableServerContextBuilders.Empty reuse() throws IllegalStateException;

	/**
	 * Returns the {@link Role} currently assumed by this object.
	 * 
	 * @return the role
	 */
	public abstract Role getRole();

	/**
	 * Instantiates a new {@link ReusableServerContext} using an appropriate
	 * implementation.
	 * 
	 * @return a {@link ReusableServerContextBuilders.Empty} instance that can
	 *         be used to initialize this object
	 */
	public static ReusableServerContextBuilders.Empty empty() {
		return new ReusableServerContextImpl();
	}

}
