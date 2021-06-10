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

package ru.windcorp.progressia.client.world.cro;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;

/**
 * Chunk render optimizer (CRO) is an object that produces optimized models for
 * chunks. CROs are sequentially given information about the blocks and tiles of
 * a particular chunk, after which they are expected to produce a set of
 * {@link Renderable}s. As the name suggests, CROs are primarily expected to
 * output models that are optimized compared to models of individual blocks and
 * tiles. An example of a CRO is {@link ChunkRenderOptimizerSurface}: it removes
 * block surfaces and tiles that it knows cannot be seen, thus significantly
 * reducing total polygon count.
 * <h3>CRO lifecycle</h3> A CRO instance is created by
 * {@link ChunkRenderOptimizerRegistry}. It may then be used to work on multiple
 * chunks sequentially. Each chunk is processed in the following way:
 * <ol>
 * <li>{@link #setup(ChunkRender)} is invoked to provide the {@link ChunkRender}
 * instance.</li>
 * <li>{@link #startRender()} is invoked. The CRO must reset its state.</li>
 * <li>{@link #addBlock(BlockRender, Vec3i)} and
 * {@link #addTile(TileRender, Vec3i, BlockFace)} are invoked for each block and
 * tile that this CRO should optimize. {@code addTile} specifies tiles in order
 * of ascension within a tile stack.</li>
 * <li>{@link #endRender()} is invoked. The CRO may perform any pending
 * calculations. The result of the optimization is returned.</li>
 * </ol>
 * <p>
 * Each CRO instance is accessed by a single thread.
 */
public abstract class ChunkRenderOptimizer extends Namespaced {

	/**
	 * The chunk that this CRO is currently working on.
	 */
	protected ChunkRender chunk = null;

	/**
	 * Creates a new CRO instance with the specified ID.
	 * 
	 * @param id
	 *            the ID of this CRO
	 */
	public ChunkRenderOptimizer(String id) {
		super(id);
	}

	/**
	 * This method is invoked before a new chunk processing cycle begins to
	 * specify the chunk. When overriding, {@code super.setup(chunk)} must be
	 * invoked.
	 * 
	 * @param chunk
	 *            the chunk that will be processed next
	 */
	public void setup(ChunkRender chunk) {
		this.chunk = chunk;
	}

	/**
	 * @return the chunk that this CRO is currently working on
	 */
	public ChunkRender getChunk() {
		return chunk;
	}

	/**
	 * Resets this CRO to a state in which a new chunk may be processed.
	 */
	public abstract void startRender();

	/**
	 * Requests that this CRO processes the provided block. This method may only
	 * be invoked between {@link #startRender()} and {@link #endRender()}. This
	 * method is only invoked once per block. This method is not necessarily
	 * invoked for each block.
	 * 
	 * @param block
	 *            a {@link BlockRender} instance describing the block. It
	 *            corresponds to {@code getChunk().getBlock(blockInChunk)}.
	 * @param blockInChunk
	 *            the position of the block
	 */
	public abstract void addBlock(BlockRender block, Vec3i blockInChunk);

	/**
	 * Requests that this CRO processes the provided tile. This method may only
	 * be invoked between {@link #startRender()} and {@link #endRender()}. This
	 * method is only invoked once per tile. This method is not necessarily
	 * invoked for each tile. When multiple tiles in a tile stack are requested,
	 * this method is invoked for lower tiles first.
	 * 
	 * @param tile
	 *            a {@link BlockRender} instance describing the tile
	 * @param blockInChunk
	 *            the position of the block that the tile belongs to
	 * @param blockFace
	 *            the face that the tile belongs to
	 */
	public abstract void addTile(TileRender tile, Vec3i blockInChunk, BlockFace blockFace);

	/**
	 * Requests that the CRO assembles and outputs its model. This method may
	 * only be invoked after {@link #startRender()}.
	 * 
	 * @return the assembled {@link Renderable}.
	 */
	public abstract Renderable endRender();

}
