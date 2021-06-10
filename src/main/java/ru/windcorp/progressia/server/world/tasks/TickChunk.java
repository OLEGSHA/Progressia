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
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.TickContextMutable;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.TSTickContext;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import static ru.windcorp.progressia.common.world.ChunkData.BLOCKS_PER_CHUNK;

public class TickChunk extends Evaluation {

	private static final int CHUNK_VOLUME = ChunkData.BLOCKS_PER_CHUNK * ChunkData.BLOCKS_PER_CHUNK
			* ChunkData.BLOCKS_PER_CHUNK;

	private final List<Consumer<Server>> randomTickMethods;

	{
		List<Consumer<Server>> randomTickMethods = new ArrayList<>();
		randomTickMethods.add(this::tickRandomBlock);

		for (BlockFace face : BlockFace.getFaces()) {
			randomTickMethods.add(s -> this.tickRandomTile(s, face));
		}

		this.randomTickMethods = ImmutableList.copyOf(randomTickMethods);
	}

	private final ChunkLogic chunk;

	public TickChunk(ChunkLogic chunk) {
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

		TickContextMutable context = TickContextMutable.uninitialized();

		chunk.forEachTickingBlock((blockInChunk, block) -> {
			context.rebuild().withChunk(chunk).withBlockInChunk(blockInChunk).build();
			((TickableBlock) block).tick(context);
		});
	}

	private void tickRegularTiles(Server server) {
		if (!chunk.hasTickingTiles())
			return;

		TickContextMutable context = TickContextMutable.uninitialized();

		chunk.forEachTickingTile((ref, tile) -> {
			context.rebuild().withServer(server).withTile(ref);
			((TickableTile) tile).tick(context);
		});
	}

	private void tickRandom(Server server) {
		float ticks = computeRandomTicks(server);

		/*
		 * If we are expected to run 3.25 random ticks per tick on average, then
		 * run 3 random ticks unconditionally and run one extra random tick with
		 * 0.25 chance
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
		randomTickMethods.get(server.getAdHocRandom().nextInt(randomTickMethods.size())).accept(server);
	}

	private void tickRandomBlock(Server server) {
		Random random = server.getAdHocRandom();

		Vec3i blockInChunk = new Vec3i(random.nextInt(BLOCKS_PER_CHUNK), random.nextInt(BLOCKS_PER_CHUNK),
				random.nextInt(BLOCKS_PER_CHUNK));

		BlockLogic block = this.chunk.getBlock(blockInChunk);

		if (!(block instanceof TickableBlock))
			return;
		TickableBlock tickable = (TickableBlock) block;

		TickContextMutable context = TickContextMutable.start().withChunk(chunk).withBlockInChunk(blockInChunk).build();

		if (tickable.getTickingPolicy(context) != TickingPolicy.RANDOM)
			return;
		tickable.tick(context);
	}

	private void tickRandomTile(Server server, BlockFace face) {
		Random random = server.getAdHocRandom();

		Vec3i blockInChunk = new Vec3i(random.nextInt(BLOCKS_PER_CHUNK), random.nextInt(BLOCKS_PER_CHUNK),
				random.nextInt(BLOCKS_PER_CHUNK));

		TileDataStack tiles = this.chunk.getData().getTilesOrNull(blockInChunk, face);
		if (tiles == null || tiles.isEmpty())
			return;

		TSTickContext context = TickContextMutable.start().withServer(server).withTS(tiles).build();

		context.forEachTile(tctxt -> {
			TileLogic logic = tctxt.getTile();
			if (!(logic instanceof TickableTile))
				return;
			TickableTile tickable = (TickableTile) logic;

			if (tickable.getTickingPolicy(tctxt) != TickingPolicy.RANDOM)
				return;
			tickable.tick(tctxt);
		});
	}

	private float computeRandomTicks(Server server) {
		return (float) (server.getTickingSettings().getRandomTickFrequency() * CHUNK_VOLUME * randomTickMethods.size()
				* server.getTickLength());
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Vec3i p = chunk.getData().getPosition();
		output.set(p.x, p.y, p.z);
	}

}
