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
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.DefaultChunkLogic;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerContexts;
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

	private final List<Consumer<ServerWorldContext>> randomTickMethods;

	{
		List<Consumer<ServerWorldContext>> randomTickMethods = new ArrayList<>();
		randomTickMethods.add(this::tickRandomBlock);

		for (AbsFace face : AbsFace.getFaces()) {
			randomTickMethods.add(context -> this.tickRandomTile(face, context));
		}

		this.randomTickMethods = ImmutableList.copyOf(randomTickMethods);
	}

	private final DefaultChunkLogic chunk;
	private ServerWorldContext context;

	public TickChunk(DefaultChunkLogic chunk) {
		this.chunk = chunk;
	}

	@Override
	public void evaluate(Server server) {
		if (context == null || context.getServer() != server) {
			context = server.createContext(chunk.getUp());
		}
		
		tickRegulars(context);
		tickRandom(context);
	}

	private void tickRegulars(ServerWorldContext context) {
		tickRegularBlocks(context);
		tickRegularTiles(context);
	}

	private void tickRegularBlocks(ServerWorldContext context) {
		if (!chunk.hasTickingBlocks())
			return;

		chunk.forEachTickingBlock((blockInChunk, block) -> {
			((TickableBlock) block).tick(ServerContexts.pushAbs(context, chunk, blockInChunk));
			context.pop();
		});
	}

	private void tickRegularTiles(ServerWorldContext context) {
		if (!chunk.hasTickingTiles())
			return;

		chunk.forEachTickingTile((ref, tile) -> {
			((TickableTile) tile).tick(ServerContexts.pushAbs(context, chunk.getUp(), ref));
			context.pop();
		});
	}

	private void tickRandom(ServerWorldContext context) {
		float ticks = computeRandomTicks(context.getServer());

		/*
		 * If we are expected to run 3.25 random ticks per tick
		 * on average, then run 3 random ticks unconditionally
		 * and run one extra random tick with 0.25 chance
		 */
		float unconditionalTicks = FloatMathUtil.floor(ticks);
		float extraTickChance = ticks - unconditionalTicks;

		for (int i = 0; i < unconditionalTicks; ++i) {
			tickRandomOnce(context);
		}

		if (context.getRandom().nextFloat() < extraTickChance) {
			tickRandomOnce(context);
		}
	}

	private void tickRandomOnce(ServerWorldContext context) {
		// Pick a target at random: a block or one of 3 primary block faces
		randomTickMethods.get(
			context.getRandom().nextInt(randomTickMethods.size())
		).accept(context);
	}

	private void tickRandomBlock(ServerWorldContext context) {
		Random random = context.getRandom();

		Vec3i blockInChunk = new Vec3i(
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK)
		);

		BlockLogic block = this.chunk.getBlock(blockInChunk);

		if (!(block instanceof TickableBlock))
			return;
		TickableBlock tickable = (TickableBlock) block;

		ServerBlockContext blockContext = ServerContexts.pushAbs(context, chunk, blockInChunk);

		if (tickable.getTickingPolicy(blockContext) != TickingPolicy.RANDOM)
			return;
		tickable.tick(blockContext);
		
		blockContext.pop();
	}

	private void tickRandomTile(AbsFace face, ServerWorldContext context) {
		Random random = context.getRandom();

		Vec3i blockInChunk = new Vec3i(
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK),
			random.nextInt(BLOCKS_PER_CHUNK)
		);

		TileDataStack tiles = this.chunk.getData().getTilesOrNull(blockInChunk, face);
		if (tiles == null || tiles.isEmpty())
			return;

		ServerTileStackContext tsContext = ServerContexts.pushAbs(context, chunk, blockInChunk, face);

		for (int i = 0; i < tiles.size(); ++i) {
			ServerTileContext tileContext = tsContext.push(i);
			
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
		
		tsContext.pop();
	}

	private float computeRandomTicks(Server server) {
		return (float) (server.getTickingSettings().getRandomTickFrequency() *
			CHUNK_VOLUME * randomTickMethods.size() *
			server.getTickLength());
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Vec3i p = chunk.getData().getPosition();
		output.set(p.x, p.y, p.z);
	}

}
