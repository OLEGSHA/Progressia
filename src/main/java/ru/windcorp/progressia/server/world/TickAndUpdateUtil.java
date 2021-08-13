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
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.block.UpdateableBlock;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerContexts;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileStackContext;
import ru.windcorp.progressia.server.world.context.ServerWorldContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;
import ru.windcorp.progressia.server.world.entity.EntityLogicRegistry;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.UpdateableTile;

public class TickAndUpdateUtil {

	public static void tickBlock(ServerBlockContext context) {
		BlockLogic uncheckedBlock = context.logic().getBlock();
		if (!(uncheckedBlock instanceof BlockLogic)) {
			return;
		}
		TickableBlock block = (TickableBlock) uncheckedBlock;
		try {
			block.tick(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick block %s", block);
		}
	}

	public static void tickBlock(Server server, Vec3i blockInWorld) {
		BlockLogic block = server.getWorld().getBlock(blockInWorld);
		if (!(block instanceof TickableBlock)) {
			return;
		}
		ServerBlockContext context = server.createContext(blockInWorld);
		tickBlock(context);
	}

	public static void tickTile(ServerTileContext context) {
		TileLogic uncheckedTile = context.logic().getTile();
		if (!(uncheckedTile instanceof TickableTile)) {
			return;
		}
		TickableTile tile = (TickableTile) uncheckedTile;
		try {
			tile.tick(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick tile %s", tile);
		}
	}

	public static void tickTile(Server server, Vec3i blockInWorld, AbsFace face, int layer) {
		TileLogic tile = server.getWorld().getTile(blockInWorld, face, layer);
		if (!(tile instanceof TickableTile)) {
			return;
		}
		ServerTileContext context = ServerContexts.pushAbs(server.createContext(blockInWorld), blockInWorld, face)
			.push(layer);
		tickTile(context);
	}

	public static void tickTiles(Server server, Vec3i blockInWorld, AbsFace face) {
		if (!server.getWorld().hasTiles(blockInWorld, face)) {
			return;
		}

		ServerTileStackContext context = ServerContexts.pushAbs(server.createContext(blockInWorld), blockInWorld, face);
		for (int i = 0; i < context.getTileCount(); ++i) {
			tickTile(context.push(i));
			context.pop();
		}
	}

	public static void updateBlock(ServerBlockContext context) {
		BlockLogic uncheckedBlock = context.logic().getBlock();
		if (!(uncheckedBlock instanceof BlockLogic)) {
			return;
		}
		UpdateableBlock block = (UpdateableBlock) uncheckedBlock;
		try {
			block.update(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not update block %s", block);
		}
	}

	public static void updateBlock(Server server, Vec3i blockInWorld) {
		BlockLogic block = server.getWorld().getBlock(blockInWorld);
		if (!(block instanceof UpdateableBlock)) {
			return;
		}
		ServerBlockContext context = server.createContext(blockInWorld);
		updateBlock(context);
	}

	public static void updateTile(ServerTileContext context) {
		TileLogic uncheckedTile = context.logic().getTile();
		if (!(uncheckedTile instanceof UpdateableTile)) {
			return;
		}
		UpdateableTile tile = (UpdateableTile) uncheckedTile;
		try {
			tile.update(context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick tile %s", tile);
		}
	}

	public static void updateTile(Server server, Vec3i blockInWorld, AbsFace face, int layer) {
		TileLogic tile = server.getWorld().getTile(blockInWorld, face, layer);
		if (!(tile instanceof UpdateableTile)) {
			return;
		}
		ServerTileContext context = ServerContexts.pushAbs(server.createContext(blockInWorld), blockInWorld, face)
			.push(layer);
		updateTile(context);
	}

	public static void updateTiles(Server server, Vec3i blockInWorld, AbsFace face) {
		if (!server.getWorld().hasTiles(blockInWorld, face)) {
			return;
		}

		ServerTileStackContext context = ServerContexts.pushAbs(server.createContext(blockInWorld), blockInWorld, face);
		for (int i = 0; i < context.getTileCount(); ++i) {
			updateTile(context.push(i));
			context.pop();
		}
	}

	public static void tickEntity(EntityLogic logic, EntityData data, ServerWorldContext context) {
		try {
			logic.tick(data, context);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not tick entity {}", logic);
		}
	}

	public static void tickEntity(EntityData data, Server server) {
		tickEntity(
			EntityLogicRegistry.getInstance().get(data.getId()),
			data,
			server.createContext(data.getUpFace())
		);
	}

	private TickAndUpdateUtil() {
	}

}
