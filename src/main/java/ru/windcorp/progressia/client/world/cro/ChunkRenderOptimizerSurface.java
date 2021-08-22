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

import static ru.windcorp.progressia.common.world.DefaultChunkData.BLOCKS_PER_CHUNK;
import static ru.windcorp.progressia.common.world.generic.TileGenericStackRO.TILES_PER_FACE;
import static ru.windcorp.progressia.common.world.rels.AbsFace.BLOCK_FACE_COUNT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.ShapePart;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.generic.GenericChunks;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class ChunkRenderOptimizerSurface extends ChunkRenderOptimizer {

	private static final float OVERLAY_OFFSET = 1 / 128f;

	/**
	 * A common interface to objects that can provide optimizeable surfaces.
	 * This is an internal interface; use {@link BlockOptimizedSurface} or
	 * {@link TileOptimizedSurface} instead.
	 */
	private static interface OptimizedSurface {

		/**
		 * Creates and outputs a set of shape parts that correspond to this
		 * surface. The coordinates of the face vertices must be in chunk
		 * coordinate system.
		 * 
		 * @param chunk           the chunk that contains the requested face
		 * @param relBlockInChunk the relative block in chunk
		 * @param blockFace       the requested face
		 * @param inner           whether this face should be visible from
		 *                        inside
		 *                        ({@code true}) or outside ({@code false})
		 * @param output          a consumer that the created shape parts must
		 *                        be
		 *                        given to
		 * @param offset          an additional offset that must be applied to
		 *                        all
		 *                        vertices
		 */
		void getShapeParts(
			DefaultChunkData chunk,
			Vec3i relBlockInChunk,
			RelFace blockFace,
			boolean inner,
			Consumer<ShapePart> output,
			Vec3 offset /* kostyl 156% */
		);

		/**
		 * Returns the opacity of the surface identified by the provided
		 * {@link RelFace}.
		 * Opaque surfaces prevent surfaces behind them from being included in
		 * chunk models.
		 * 
		 * @param blockFace the face to query
		 * @return {@code true} iff the surface is opaque.
		 */
		boolean isOpaque(RelFace blockFace);
	}

	/**
	 * A block that can be optimized by {@link ChunkRenderOptimizerSurface}.
	 */
	public static interface BlockOptimizedSurface extends OptimizedSurface {

		/**
		 * Returns the opacity of the block. Opaque blocks do not expect that
		 * the camera can be inside them. Opaque blocks prevent surfaces that
		 * face them
		 * from being included in chunk models.
		 * 
		 * @return {@code true} iff the block is opaque.
		 */
		boolean isBlockOpaque();
	}

	/**
	 * A tile that can be optimized by {@link ChunkRenderOptimizerSurface}.
	 */
	public static interface TileOptimizedSurface extends OptimizedSurface {
		// Empty for now
	}

	private static class BlockInfo {
		BlockOptimizedSurface block;
		final FaceInfo[] faces = new FaceInfo[BLOCK_FACE_COUNT];

		{
			for (int i = 0; i < faces.length; ++i) {
				faces[i] = new FaceInfo(this);
			}
		}
	}

	private static class FaceInfo {
		static final int BLOCK_LAYER = -1;

		final BlockInfo block;

		int topOpaqueSurface = BLOCK_LAYER;
		int bottomOpaqueSurface = Integer.MAX_VALUE;

		final TileOptimizedSurface[] tiles = new TileOptimizedSurface[TILES_PER_FACE];
		int tileCount = 0;

		FaceInfo(BlockInfo block) {
			this.block = block;
		}

		OptimizedSurface getSurface(int layer) {
			return layer == BLOCK_LAYER ? block.block : tiles[layer];
		}
	}

	private final BlockInfo[][][] data = new BlockInfo[BLOCKS_PER_CHUNK][BLOCKS_PER_CHUNK][BLOCKS_PER_CHUNK];

	public ChunkRenderOptimizerSurface(String id) {
		super(id);
	}

	@Override
	public void startRender() {
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < BLOCKS_PER_CHUNK; ++z) {
					data[x][y][z] = new BlockInfo();
				}
			}
		}
	}

	@Override
	public void addBlock(BlockRender block, Vec3i relBlockInChunk) {
		if (!(block instanceof BlockOptimizedSurface))
			return;

		BlockOptimizedSurface bos = (BlockOptimizedSurface) block;
		addBlock(relBlockInChunk, bos);
	}

	@Override
	public void addTile(TileRender tile, Vec3i relBlockInChunk, RelFace face) {
		if (!(tile instanceof TileOptimizedSurface))
			return;

		TileOptimizedSurface tos = (TileOptimizedSurface) tile;
		addTile(relBlockInChunk, face, tos);
	}

	private void addBlock(Vec3i relBlockInChunk, BlockOptimizedSurface block) {
		getBlock(relBlockInChunk).block = block;
	}

	private void addTile(Vec3i relBlockInChunk, RelFace face, TileOptimizedSurface tile) {
		FaceInfo faceInfo = getFace(relBlockInChunk, face);

		int index = faceInfo.tileCount;
		faceInfo.tileCount++;

		faceInfo.tiles[index] = tile;

		if (tile.isOpaque(face)) {
			faceInfo.topOpaqueSurface = index;

			if (faceInfo.bottomOpaqueSurface == FaceInfo.BLOCK_LAYER) {
				faceInfo.bottomOpaqueSurface = index;
			}
		}
	}

	protected BlockInfo getBlock(Vec3i relBlockInChunk) {
		return data[relBlockInChunk.x][relBlockInChunk.y][relBlockInChunk.z];
	}

	protected FaceInfo getFace(Vec3i relBlockInChunk, RelFace face) {
		return getBlock(relBlockInChunk).faces[face.getId()];
	}

	@Override
	public Renderable endRender() {
		Collection<ShapePart> shapeParts = new ArrayList<>(
			BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * 3
		);

		Consumer<ShapePart> consumer = shapeParts::add;

		GenericChunks.forEachBiC(relBlockInChunk -> {
			processInnerFaces(relBlockInChunk, consumer);
			processOuterFaces(relBlockInChunk, consumer);
		});

		if (shapeParts.isEmpty()) {
			return null;
		}

		return new Shape(
			Usage.STATIC,
			WorldRenderProgram.getDefault(),
			shapeParts.toArray(new ShapePart[shapeParts.size()])
		);
	}

	private void processOuterFaces(
		Vec3i relBlockInChunk,
		Consumer<ShapePart> output
	) {
		for (RelFace blockFace : RelFace.getFaces()) {
			processOuterFace(relBlockInChunk, blockFace, output);
		}
	}

	private void processOuterFace(Vec3i relBlockInChunk, RelFace blockFace, Consumer<ShapePart> output) {
		if (!shouldRenderOuterFace(relBlockInChunk, blockFace))
			return;

		FaceInfo info = getFace(relBlockInChunk, blockFace);

		if (info.tileCount == 0 && info.block.block == null)
			return;

		Vec3 faceOrigin = new Vec3(relBlockInChunk.x, relBlockInChunk.y, relBlockInChunk.z);
		Vec3 offset = new Vec3(blockFace.getRelFloatVector()).mul(OVERLAY_OFFSET);

		for (
			int layer = info.topOpaqueSurface;
			layer < info.tileCount;
			++layer
		) {
			OptimizedSurface surface = info.getSurface(layer);
			if (surface == null)
				continue; // layer may be BLOCK_LAYER, then block may be null

			surface.getShapeParts(chunk.getData(), relBlockInChunk, blockFace, false, output, faceOrigin);

			faceOrigin.add(offset);
		}
	}

	private void processInnerFaces(Vec3i relBlockInChunk, Consumer<ShapePart> output) {
		for (RelFace blockFace : RelFace.getFaces()) {
			processInnerFace(relBlockInChunk, blockFace, output);
		}
	}

	private void processInnerFace(Vec3i relBlockInChunk, RelFace blockFace, Consumer<ShapePart> output) {
		if (!shouldRenderInnerFace(relBlockInChunk, blockFace))
			return;

		FaceInfo info = getFace(relBlockInChunk, blockFace);

		if (info.tileCount == 0 && info.block.block == null)
			return;

		Vec3 faceOrigin = new Vec3(relBlockInChunk.x, relBlockInChunk.y, relBlockInChunk.z);
		Vec3 offset = new Vec3(blockFace.getRelFloatVector()).mul(OVERLAY_OFFSET);

		for (
			int layer = FaceInfo.BLOCK_LAYER;
			layer <= info.bottomOpaqueSurface && layer < info.tileCount;
			++layer
		) {
			OptimizedSurface surface = info.getSurface(layer);
			if (surface == null)
				continue; // layer may be BLOCK_LAYER, then block may be null

			surface.getShapeParts(chunk.getData(), relBlockInChunk, blockFace, true, output, faceOrigin);

			faceOrigin.add(offset);
		}
	}

	private boolean shouldRenderOuterFace(Vec3i relBlockInChunk, RelFace face) {
		relBlockInChunk.add(face.getRelVector());
		try {
			return shouldRenderWhenFacing(relBlockInChunk, face);
		} finally {
			relBlockInChunk.sub(face.getRelVector());
		}
	}

	private boolean shouldRenderInnerFace(Vec3i relBlockInChunk, RelFace face) {
		return shouldRenderWhenFacing(relBlockInChunk, face);
	}

	private boolean shouldRenderWhenFacing(Vec3i relBlockInChunk, RelFace face) {
		if (GenericChunks.containsBiC(relBlockInChunk)) {
			return shouldRenderWhenFacingLocal(relBlockInChunk, face);
		} else {
			return shouldRenderWhenFacingNeighbor(relBlockInChunk, face);
		}
	}

	private boolean shouldRenderWhenFacingLocal(Vec3i relBlockInChunk, RelFace face) {
		BlockOptimizedSurface block = getBlock(relBlockInChunk).block;

		if (block == null) {
			return true;
		}
		if (block.isOpaque(face)) {
			return false;
		}

		return true;
	}

	private boolean shouldRenderWhenFacingNeighbor(Vec3i relBlockInLocalChunk, RelFace face) {
		Vec3i blockInChunk = Vectors.grab3i();
		chunk.resolve(relBlockInLocalChunk, blockInChunk);
		Vec3i chunkPos = Vectors.grab3i().set(chunk.getX(), chunk.getY(), chunk.getZ());

		try {
			// Determine blockInChunk and chunkPos
			if (blockInChunk.x == -1) {
				blockInChunk.x = BLOCKS_PER_CHUNK - 1;
				chunkPos.x -= 1;
			} else if (blockInChunk.x == BLOCKS_PER_CHUNK) {
				blockInChunk.x = 0;
				chunkPos.x += 1;
			} else if (blockInChunk.y == -1) {
				blockInChunk.y = BLOCKS_PER_CHUNK - 1;
				chunkPos.y -= 1;
			} else if (blockInChunk.y == BLOCKS_PER_CHUNK) {
				blockInChunk.y = 0;
				chunkPos.y += 1;
			} else if (blockInChunk.z == -1) {
				blockInChunk.z = BLOCKS_PER_CHUNK - 1;
				chunkPos.z -= 1;
			} else if (blockInChunk.z == BLOCKS_PER_CHUNK) {
				blockInChunk.z = 0;
				chunkPos.z += 1;
			} else {
				throw new AssertionError(
					"Requested incorrent neighbor ("
						+ relBlockInLocalChunk.x + "; "
						+ relBlockInLocalChunk.y + "; "
						+ relBlockInLocalChunk.z + ")"
				);
			}

			ChunkRender chunk = this.chunk.getWorld().getChunk(chunkPos);
			if (chunk == null)
				return false;

			BlockRender block = chunk.getBlock(blockInChunk);
			if (!(block instanceof BlockOptimizedSurface))
				return true;

			BlockOptimizedSurface bos = (BlockOptimizedSurface) block;
			RelFace rotatedFace = face.rotate(this.chunk.getUp(), chunk.getUp());

			if (!bos.isOpaque(rotatedFace)) {
				return true;
			}

			return false;

		} finally {
			Vectors.release(blockInChunk);
			Vectors.release(chunkPos);
		}
	}

}
