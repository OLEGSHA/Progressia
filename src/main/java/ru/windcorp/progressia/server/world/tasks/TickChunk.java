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

package ru.windcorp.progressia.server.world.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.FloatMathUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.generic.ChunkGenericRO;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.DefaultChunkLogic;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileStackContext;
import ru.windcorp.progressia.server.world.context.ServerWorldContext;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import static ru.windcorp.progressia.common.world.DefaultChunkData.BLOCKS_PER_CHUNK;

public class TickChunk extends Evaluation {

	private static final int CHUNK_VOLUME = DefaultChunkData.BLOCKS_PER_CHUNK *
		DefaultChunkData.BLOCKS_PER_CHUNK *
		DefaultChunkData.BLOCKS_PER_CHUNK;

	private final List<Consumer<Server>> randomTickMethods;

	{
		List<Consumer<Server>> randomTickMethods = new ArrayList<>();
		randomTickMethods.add(this::tickRandomBlock);

		for (AbsFace face : AbsFace.getFaces()) {
			randomTickMethods.add(s -> this.tickRandomTile(s, face));
		}

		this.randomTickMethods = ImmutableList.copyOf(randomTickMethods);
	}

	private final DefaultChunkLogic chunk;

	public TickChunk(DefaultChunkLogic chunk) {
		this.chunk = chunk;
	}

	@Override
	public void evaluate(Server server) {
		tickRegulars(server);
		tickRandom(server);
	}

	private void tickRegulars(Server server) {
		tickRegularBlocks(server);
		tickRegularTiles(server);
	}

	private void tickRegularBlocks(Server server) {
		if (!chunk.hasTickingBlocks())
			return;

		ServerWorldContext context = server.createContext();

		chunk.forEachTickingBlock((blockInChunk, block) -> {
			((TickableBlock) block).tick(contextPushBiC(context, chunk, blockInChunk));
			context.pop();
		});
	}

	private void tickRegularTiles(Server server) {
		if (!chunk.hasTickingTiles())
			return;

		ServerWorldContext context = server.createContext();
		Vec3i blockInWorld = new Vec3i();

		chunk.forEachTickingTile((ref, tile) -> {
			((TickableTile) tile).tick(
				context.push(ref.getStack().getBlockInWorld(blockInWorld), ref.getStack().getFace(), ref.getIndex())
			);
			context.pop();
		});
	}

	private void tickRandom(Server server) {
		float ticks = computeRandomTicks(server);

		/*
		 * If we are expected to run 3.25 random ticks per tick
		 * on average, then run 3 random ticks unconditionally
		 * and run one extra random tick with 0.25 chance
		 */
		float unconditionalTicks = FloatMathUtil.floor(ticks);
		float extraTickChance = ticks - unconditionalTicks;

		for (int i = 0; i < unconditionalTicks; ++i) {
			tickRandomOnce(server);
		}

		if (server.getAdHocRandom().nextFloat() < extraTickChance) {
			tickRandomOnce(server);
		}
	}

	private void tickRandomOnce(Server server) {
		// Pick a target at random: a block or one of 3 primary block faces
		randomTickMethods.get(
			server.getAdHocRandom().nextInt(randomTickMethods.size())
		).accept(server);
	}

	private void tickRandomBlock(Server server) {
		Random random = server.getAdHocRandom();

		Vec3i blockInChunk = new Vec3i(
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK)
		);

		BlockLogic block = this.chunk.getBlock(blockInChunk);

		if (!(block instanceof TickableBlock))
			return;
		TickableBlock tickable = (TickableBlock) block;

		ServerBlockContext context = contextPushBiC(server.createContext(), chunk, blockInChunk);

		if (tickable.getTickingPolicy(context) != TickingPolicy.RANDOM)
			return;
		tickable.tick(context);
	}

	private void tickRandomTile(Server server, AbsFace face) {
		Random random = server.getAdHocRandom();

		Vec3i blockInChunk = new Vec3i(
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK)
		);

		TileDataStack tiles = this.chunk.getData().getTilesOrNull(blockInChunk, face);
		if (tiles == null || tiles.isEmpty())
			return;

		ServerTileStackContext context = contextPushBiC(server.createContext(), chunk, blockInChunk).push(face.relativize(chunk.getUp()));

		for (int i = 0; i < tiles.size(); ++i) {
			ServerTileContext tileContext = context.push(i);
			
			TileLogic logic = tileContext.logic().getTile();
			if (!(logic instanceof TickableTile)) {
				tileContext.pop();
				continue;
			}
			TickableTile tickable = (TickableTile) logic;

			if (tickable.getTickingPolicy(tileContext) != TickingPolicy.RANDOM) {
				tileContext.pop();
				continue;
			}
			tickable.tick(tileContext);
			
			tileContext.pop();
		}
	}

	private float computeRandomTicks(Server server) {
		return (float) (server.getTickingSettings().getRandomTickFrequency() *
			CHUNK_VOLUME * randomTickMethods.size() *
			server.getTickLength());
	}

	private ServerBlockContext contextPushBiC(
		ServerWorldContext context,
		ChunkGenericRO<?, ?, ?, ?, ?> chunk,
		Vec3i blockInChunk
	) {
		Vec3i blockInWorld = Vectors.grab3i();
		Coordinates.getInWorld(chunk.getPosition(), blockInChunk, blockInWorld);
		ServerBlockContext blockContext = context.push(blockInWorld);
		Vectors.release(blockInWorld);
		return blockContext;
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Vec3i p = chunk.getData().getPosition();
		output.set(p.x, p.y, p.z);
	}

}
