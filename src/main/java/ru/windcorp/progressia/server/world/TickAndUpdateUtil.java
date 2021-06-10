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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.block.UpdateableBlock;
import ru.windcorp.progressia.server.world.entity.EntityLogic;
import ru.windcorp.progressia.server.world.entity.EntityLogicRegistry;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileTickContext;
import ru.windcorp.progressia.server.world.tile.UpdateableTile;

public class TickAndUpdateUtil {

	public static void tickBlock(TickableBlock block, BlockTickContext context) {
		try {
			block.tick(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick block %s", block);
		}
	}

	public static void tickBlock(WorldLogic world, Vec3i blockInWorld) {
		BlockLogic block = world.getBlock(blockInWorld);
		if (!(block instanceof TickableBlock))
			return; // also checks nulls

		BlockTickContext tickContext = TickContextMutable.start().withWorld(world).withBlock(blockInWorld).build();
		tickBlock((TickableBlock) block, tickContext);
	}

	public static void tickTile(TickableTile tile, TileTickContext context) {
		try {
			tile.tick(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick tile %s", tile);
		}
	}

	public static void tickTile(WorldLogic world, Vec3i blockInWorld, BlockFace face, int layer) {
		TileLogic tile = world.getTile(blockInWorld, face, layer);
		if (!(tile instanceof TickableTile))
			return;

		TileTickContext tickContext = TickContextMutable.start().withWorld(world).withBlock(blockInWorld).withFace(face)
				.withLayer(layer);
		tickTile((TickableTile) tile, tickContext);
	}

	public static void tickTiles(WorldLogic world, Vec3i blockInWorld, BlockFace face) {
		TickContextMutable.start().withWorld(world).withBlock(blockInWorld).withFace(face).build()
				.forEachTile(context -> {
					TileLogic tile = context.getTile();
					if (tile instanceof TickableTile) {
						tickTile((TickableTile) tile, context);
					}
				});
	}

	public static void updateBlock(UpdateableBlock block, BlockTickContext context) {
		try {
			block.update(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not update block {}", block);
		}
	}

	public static void updateBlock(WorldLogic world, Vec3i blockInWorld) {
		BlockLogic block = world.getBlock(blockInWorld);
		if (!(block instanceof UpdateableBlock))
			return; // also checks nulls

		BlockTickContext tickContext = TickContextMutable.start().withWorld(world).withBlock(blockInWorld).build();
		updateBlock((UpdateableBlock) block, tickContext);
	}

	public static void updateTile(UpdateableTile tile, TileTickContext context) {
		try {
			tile.update(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not update tile {}", tile);
		}
	}

	public static void updateTile(WorldLogic world, Vec3i blockInWorld, BlockFace face, int layer) {
		TileLogic tile = world.getTile(blockInWorld, face, layer);
		if (!(tile instanceof UpdateableTile))
			return;

		TileTickContext tickContext = TickContextMutable.start().withWorld(world).withBlock(blockInWorld).withFace(face)
				.withLayer(layer);
		updateTile((UpdateableTile) tile, tickContext);
	}

	public static void updateTiles(WorldLogic world, Vec3i blockInWorld, BlockFace face) {
		TickContextMutable.start().withWorld(world).withBlock(blockInWorld).withFace(face).build()
				.forEachTile(context -> {
					TileLogic tile = context.getTile();
					if (tile instanceof UpdateableTile) {
						updateTile((UpdateableTile) tile, context);
					}
				});
	}

	public static void tickEntity(EntityLogic logic, EntityData data, TickContext context) {
		try {
			logic.tick(data, context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick entity {}", logic);
		}
	}

	public static void tickEntity(EntityData data, Server server) {
		tickEntity(EntityLogicRegistry.getInstance().get(data.getId()), data,
				TickContextMutable.start().withServer(server).build());
	}

	private TickAndUpdateUtil() {
	}

}
