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
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;

public class DefaultServerContextLogic implements ServerTileContext.Logic {
	
	private final ServerTileContext parent;

	public DefaultServerContextLogic(ServerTileContext parent) {
		this.parent = parent;
	}
	
	@Override
	public ServerTileContext data() {
		return parent;
	}

	@Override
	public Server getServer() {
		return parent.getServer();
	}

	@Override
	public Random getRandom() {
		return parent.getRandom();
	}

	@Override
	public BlockLogic getBlock(Vec3i location) {
		BlockData data = parent.getBlock(location);
		return data == null ? null : BlockLogicRegistry.getInstance().get(data.getId());
	}
	
	@Override
	public boolean isLocationLoaded(Vec3i location) {
		return parent.isLocationLoaded(location);
	}

	@Override
	public ServerTileContext.Logic push(Vec3i location) {
		parent.push(location);
		return this;
	}

	@Override
	public ServerTileContext.Logic push(Vec3i location, RelFace face) {
		parent.push(location, face);
		return this;
	}

	@Override
	public ServerTileContext.Logic push(Vec3i location, RelFace face, int layer) {
		parent.push(location, face, layer);
		return this;
	}

	@Override
	public double getTickLength() {
		return parent.getTickLength();
	}

	@Override
	public TileLogic getTile(Vec3i location, BlockFace face, int layer) {
		TileData data = parent.getTile(location, face, layer);
		return data == null ? null : TileLogicRegistry.getInstance().get(data.getId());
	}

	@Override
	public TileLogic getTileByTag(Vec3i location, BlockFace face, int tag) {
		TileData data = parent.getTileByTag(location, face, tag);
		return data == null ? null : TileLogicRegistry.getInstance().get(data.getId());
	}

	@Override
	public Vec3i getLocation() {
		return parent.getLocation();
	}

	@Override
	public boolean hasTile(Vec3i location, BlockFace face, int layer) {
		return parent.hasTile(location, face, layer);
	}

	@Override
	public boolean isTagValid(Vec3i location, BlockFace face, int tag) {
		return parent.isTagValid(location, face, tag);
	}

	@Override
	public boolean isReal() {
		return parent.isReal();
	}

	@Override
	public int getTileCount(Vec3i location, BlockFace face) {
		return parent.getTileCount(location, face);
	}

	@Override
	public Collection<EntityData> getEntities() {
		return parent.getEntities();
	}

	@Override
	public EntityData getEntity(long entityId) {
		return parent.getEntity(entityId);
	}

	@Override
	public void pop() {
		parent.pop();
	}

	@Override
	public RelFace getFace() {
		return parent.getFace();
	}

	@Override
	public int getLayer() {
		return parent.getLayer();
	}

	@Override
	public int getTag() {
		return parent.getTag();
	}

	@Override
	public String toString() {
		return parent + ".Logic";
	}

}
