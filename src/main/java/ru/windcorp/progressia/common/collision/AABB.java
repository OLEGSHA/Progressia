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

package ru.windcorp.progressia.common.collision;

import glm.vec._3.Vec3;

/**
 * An implementation of an <a href=
 * "https://en.wikipedia.org/wiki/Minimum_bounding_box#Axis-aligned_minimum_bounding_box">Axis-Aligned
 * Bounding Box</a>.
 * 
 * @author javapony
 */
public class AABB implements AABBoid {

	private class AABBWallImpl implements Wall {

		private final Vec3 originOffset = new Vec3();
		private final Vec3 widthSelector = new Vec3();
		private final Vec3 heightSelector = new Vec3();

		public AABBWallImpl(float ox, float oy, float oz, float wx, float wy, float wz, float hx, float hy, float hz) {
			this.originOffset.set(ox, oy, oz);
			this.widthSelector.set(wx, wy, wz);
			this.heightSelector.set(hx, hy, hz);
		}

		@Override
		public void getOrigin(Vec3 output) {
			output.set(originOffset).mul(AABB.this.getSize()).add(AABB.this.getOrigin());
		}

		@Override
		public void getWidth(Vec3 output) {
			output.set(AABB.this.getSize()).mul(widthSelector);
		}

		@Override
		public void getHeight(Vec3 output) {
			output.set(AABB.this.getSize()).mul(heightSelector);
		}

	}

	public static final AABB UNIT_CUBE = new AABB(0, 0, 0, 1, 1, 1);

	private final Wall[] walls = new Wall[] { new AABBWallImpl(-0.5f, -0.5f, +0.5f, +1, 0, 0, 0, +1, 0), // Top
			new AABBWallImpl(-0.5f, -0.5f, -0.5f, 0, +1, 0, +1, 0, 0), // Bottom
			new AABBWallImpl(+0.5f, -0.5f, -0.5f, 0, +1, 0, 0, 0, +1), // North
			new AABBWallImpl(-0.5f, +0.5f, -0.5f, 0, -1, 0, 0, 0, +1), // South
			new AABBWallImpl(+0.5f, +0.5f, -0.5f, -1, 0, 0, 0, 0, +1), // West
			new AABBWallImpl(-0.5f, -0.5f, -0.5f, +1, 0, 0, 0, 0, +1) // East
	};

	private final Vec3 origin = new Vec3();
	private final Vec3 size = new Vec3();

	public AABB(Vec3 origin, Vec3 size) {
		this(origin.x, origin.y, origin.z, size.x, size.y, size.z);
	}

	public AABB(float ox, float oy, float oz, float xSize, float ySize, float zSize) {
		this.origin.set(ox, oy, oz);
		this.size.set(xSize, ySize, zSize);
	}

	public Vec3 getOrigin() {
		return origin;
	}

	@Override
	public void getOrigin(Vec3 output) {
		output.set(origin);
	}

	@Override
	public void setOrigin(Vec3 origin) {
		this.origin.set(origin);
	}

	@Override
	public void moveOrigin(Vec3 displacement) {
		this.origin.add(displacement);
	}

	public Vec3 getSize() {
		return size;
	}

	@Override
	public void getSize(Vec3 output) {
		output.set(size);
	}

	public void setSize(Vec3 size) {
		setSize(size.x, size.y, size.z);
	}

	public void setSize(float xSize, float ySize, float zSize) {
		this.size.set(xSize, ySize, zSize);
	}

	@Override
	public Wall getWall(int faceId) {
		// No, we don't support Apple.
		return walls[faceId];
	}

}
