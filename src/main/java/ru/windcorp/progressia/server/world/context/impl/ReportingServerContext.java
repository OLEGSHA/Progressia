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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.context.ServerTileContext;

public class ReportingServerContext extends FilterServerContext {

	public static interface ChangeListener {

		void onBlockSet(Vec3i location, BlockData block);

		void onTileAdded(Vec3i location, BlockFace face, TileData tile);

		void onTileRemoved(Vec3i location, BlockFace face, int tag);

		void onEntityAdded(EntityData entity);

		void onEntityRemoved(long entityId);

		<SE extends StatefulObject & EntityGeneric> void onEntityChanged(SE entity, StateChange<SE> change);

		void onTimeChanged(float change);

	}

	private ChangeListener listener = null;
	private boolean passToParent = true;

	/**
	 * Creates a new {@link ReportingServerContext} instance that delegates
	 * method calls to the specified parent context. Write methods are always
	 * passed, disable with {@link #setPassToParent(boolean)}. No listener is
	 * set, set a listener with {@link #withListener(ChangeListener)}.
	 * 
	 * @param parent the parent context
	 */
	public ReportingServerContext(ServerTileContext parent) {
		super(parent);
	}

	public ReportingServerContext withListener(ChangeListener listener) {
		this.listener = listener;
		return this;
	}

	public ReportingServerContext setPassToParent(boolean pass) {
		this.passToParent = pass;
		return this;
	}

	@Override
	public void setBlock(Vec3i location, BlockData block) {
		if (passToParent) {
			super.setBlock(location, block);
		}
		if (listener != null) {
			listener.onBlockSet(location, block);
		}
	}

	@Override
	public void addTile(Vec3i location, BlockFace face, TileData tile) {
		if (passToParent) {
			super.addTile(location, face, tile);
		}
		if (listener != null) {
			listener.onTileAdded(location, face, tile);
		}
	}

	@Override
	public void removeTile(Vec3i location, BlockFace face, int tag) {
		if (passToParent) {
			super.removeTile(location, face, tag);
		}
		if (listener != null) {
			listener.onTileRemoved(location, face, tag);
		}
	}

	@Override
	public void addEntity(EntityData entity) {
		if (passToParent) {
			super.addEntity(entity);
		}
		if (listener != null) {
			listener.onEntityAdded(entity);
		}
	}

	@Override
	public void removeEntity(long entityId) {
		if (passToParent) {
			super.removeEntity(entityId);
		}
		if (listener != null) {
			listener.onEntityRemoved(entityId);
		}
	}

	@Override
	public <SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change) {
		if (passToParent) {
			super.changeEntity(entity, change);
		}
		if (listener != null) {
			listener.onEntityChanged(entity, change);
		}
	}

	@Override
	public void advanceTime(float change) {
		if (passToParent) {
			super.advanceTime(change);
		}
		if (listener != null) {
			listener.onTimeChanged(change);
		}
	}

}
