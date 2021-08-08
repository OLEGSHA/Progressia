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
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileStackContext;

/**
 * This is an implementation of the server context tree that delegates all calls
 * to a provided instance of {@link ServerTileContext}.
 */
public abstract class FilterServerContext implements ServerTileContext {

	protected final ServerTileContext parent;
	protected final DefaultServerContextLogic logic = new DefaultServerContextLogic(this);

	public FilterServerContext(ServerTileContext parent) {
		this.parent = parent;
	}
	
	public ServerTileContext getParent() {
		return parent;
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
	public RelFace getFace() {
		return parent.getFace();
	}

	@Override
	public Vec3i getLocation() {
		return parent.getLocation();
	}

	@Override
	public boolean isReal() {
		return parent.isReal();
	}

	@Override
	public void pop() {
		parent.pop();
	}

	@Override
	public boolean isImmediate() {
		return parent.isImmediate();
	}

	@Override
	public void addTile(Vec3i location, BlockFace face, TileData tile) {
		parent.addTile(location, face, tile);
	}

	@Override
	public void removeTile(Vec3i location, BlockFace face, int tag) {
		parent.removeTile(location, face, tag);
	}

	@Override
	public void addEntity(EntityData entity) {
		parent.addEntity(entity);
	}

	@Override
	public void removeEntity(long entityId) {
		parent.removeEntity(entityId);
	}

	@Override
	public <SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change) {
		parent.changeEntity(entity, change);
	}

	@Override
	public void advanceTime(float change) {
		parent.advanceTime(change);
	}

	@Override
	public float getTime() {
		return parent.getTime();
	}

	@Override
	public GravityModel getGravityModel() {
		return parent.getGravityModel();
	}

	@Override
	public BlockData getBlock(Vec3i location) {
		return parent.getBlock(location);
	}

	@Override
	public boolean isLocationLoaded(Vec3i location) {
		return parent.isLocationLoaded(location);
	}

	@Override
	public TileData getTile(Vec3i location, BlockFace face, int layer) {
		return parent.getTile(location, face, layer);
	}

	@Override
	public TileData getTileByTag(Vec3i location, BlockFace face, int tag) {
		return parent.getTileByTag(location, face, tag);
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
	public ServerBlockContext push(Vec3i location) {
		return parent.push(location);
	}

	@Override
	public ServerTileStackContext push(Vec3i location, RelFace face) {
		return parent.push(location, face);
	}

	@Override
	public ServerTileContext push(Vec3i location, RelFace face, int layer) {
		return parent.push(location, face, layer);
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
	public double getTickLength() {
		return parent.getTickLength();
	}

	@Override
	public ServerTileContext.Logic logic() {
		return logic;
	}

}
