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

import java.util.ArrayList;
import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileStackContext;

public abstract class TransformingServerContext extends FilterServerContext {

	private final Vec3i location = new Vec3i();
	private boolean isLocationValid = false;

	private RelFace face = null;

	private final List<Vec3i> vectorCache = new ArrayList<>(1);

	public TransformingServerContext(ServerTileContext parent) {
		super(parent);
	}

	protected abstract void transform(Vec3i userLocation, Vec3i output);

	protected abstract void untransform(Vec3i parentLocation, Vec3i output);

	protected abstract RelFace transform(RelFace userFace);

	protected abstract RelFace untransform(RelFace parentFace);

	protected void invalidateCache() {
		isLocationValid = false;
		face = null;
	}

	private Vec3i grabVector(Vec3i userVector) {
		Vec3i parentVector;

		if (vectorCache.isEmpty()) {
			parentVector = new Vec3i();
		} else {
			parentVector = vectorCache.remove(vectorCache.size() - 1);
		}

		transform(userVector, parentVector);

		return parentVector;
	}

	private void releaseVector(Vec3i parentVector) {
		vectorCache.add(parentVector);
	}

	@Override
	public Vec3i getLocation() {
		// Always invoke parent method to allow parent to determine validity
		Vec3i parentLocation = super.getLocation();

		if (!isLocationValid) {
			untransform(parentLocation, location);
			isLocationValid = true;
		}

		return location;
	}

	@Override
	public RelFace getFace() {
		// Always invoke parent method to allow parent to determine validity
		RelFace parentFace = super.getFace();

		if (face == null) {
			face = untransform(parentFace);
		}

		return face;
	}

	@Override
	public void pop() {
		super.pop();
		invalidateCache();
	}

	@Override
	public ServerBlockContext push(Vec3i userLocation) {
		Vec3i parentLocation = grabVector(userLocation);
		super.push(parentLocation);
		releaseVector(parentLocation);
		
		location.set(userLocation.x, userLocation.y, userLocation.z);
		isLocationValid = true;
		face = null;
		
		return this;
	}

	@Override
	public ServerTileStackContext push(Vec3i userLocation, RelFace userFace) {
		Vec3i parentLocation = grabVector(userLocation);
		super.push(parentLocation, transform(userFace));
		releaseVector(parentLocation);
		
		location.set(userLocation.x, userLocation.y, userLocation.z);
		isLocationValid = true;
		face = userFace;
		
		return this;
	}

	@Override
	public ServerTileContext push(Vec3i userLocation, RelFace userFace, int layer) {
		Vec3i parentLocation = grabVector(userLocation);
		super.push(parentLocation, transform(userFace), layer);
		releaseVector(parentLocation);
		
		location.set(userLocation.x, userLocation.y, userLocation.z);
		isLocationValid = true;
		face = userFace;
		
		return this;
	}
	
	@Override
	public Vec3i toAbsolute(Vec3i contextLocation, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}
		
		transform(contextLocation, output);
		
		return super.toAbsolute(output, output);
	}
	
	@Override
	public Vec3i toContext(Vec3i absoluteLocation, Vec3i output) {
		output = super.toContext(absoluteLocation, output);
		untransform(output, output);
		return output;
	}
	
	@Override
	public AbsFace toAbsolute(RelFace contextFace) {
		return super.toAbsolute(transform(contextFace));
	}
	
	@Override
	public RelFace toContext(AbsFace absoluteFace) {
		return untransform(super.toContext(absoluteFace));
	}
	
	@Override
	public boolean isLocationLoaded(Vec3i userLocation) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.isLocationLoaded(parentLocation);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public BlockData getBlock(Vec3i userLocation) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.getBlock(parentLocation);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public void setBlock(Vec3i userLocation, BlockData block) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			super.setBlock(parentLocation, block);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public boolean hasTile(Vec3i userLocation, RelFace userFace, int layer) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.hasTile(parentLocation, transform(userFace), layer);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public TileData getTile(Vec3i userLocation, RelFace userFace, int layer) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.getTile(parentLocation, transform(userFace), layer);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public boolean isTagValid(Vec3i userLocation, RelFace userFace, int tag) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.isTagValid(parentLocation, transform(userFace), tag);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public TileData getTileByTag(Vec3i userLocation, RelFace userFace, int tag) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.getTileByTag(parentLocation, transform(userFace), tag);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public int getTileCount(Vec3i userLocation, RelFace userFace) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			return super.getTileCount(parentLocation, transform(userFace));
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public void addTile(Vec3i userLocation, RelFace userFace, TileData tile) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			super.addTile(parentLocation, transform(userFace), tile);
		} finally {
			releaseVector(parentLocation);
		}
	}

	@Override
	public void removeTile(Vec3i userLocation, RelFace userFace, int tag) {
		Vec3i parentLocation = grabVector(userLocation);

		try {
			super.removeTile(parentLocation, transform(userFace), tag);
		} finally {
			releaseVector(parentLocation);
		}
	}

}
