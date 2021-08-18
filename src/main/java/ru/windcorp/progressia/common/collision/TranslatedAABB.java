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
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TranslatedAABB implements AABBoid {

	private class TranslatedAABBWall implements Wall {
		private final int id;

		public TranslatedAABBWall(int id) {
			this.id = id;
		}

		@Override
		public void getOrigin(Vec3 output) {
			parent.getWall(id).getOrigin(output);
			output.add(translation);
		}

		@Override
		public void getWidth(Vec3 output) {
			parent.getWall(id).getWidth(output);
		}

		@Override
		public void getHeight(Vec3 output) {
			parent.getWall(id).getHeight(output);
		}
	}

	private AABBoid parent;
	private final Vec3 translation = new Vec3();

	private final TranslatedAABBWall[] walls = new TranslatedAABBWall[BlockFace.BLOCK_FACE_COUNT];

	{
		for (int id = 0; id < walls.length; ++id) {
			walls[id] = new TranslatedAABBWall(id);
		}
	}

	public TranslatedAABB(AABBoid parent, float tx, float ty, float tz) {
		setParent(parent);
		setTranslation(tx, ty, tz);
	}

	public TranslatedAABB(AABBoid parent, Vec3 translation) {
		this(parent, translation.x, translation.y, translation.z);
	}

	public TranslatedAABB() {
		this(null, 0, 0, 0);
	}

	@Override
	public void setOrigin(Vec3 origin) {
		Vec3 v = Vectors.grab3().set(origin).sub(translation);
		parent.setOrigin(v);
		Vectors.release(v);
	}

	@Override
	public void moveOrigin(Vec3 displacement) {
		parent.moveOrigin(displacement);
	}

	@Override
	public void getOrigin(Vec3 output) {
		parent.getOrigin(output);
		output.add(translation);
	}

	@Override
	public void getSize(Vec3 output) {
		parent.getSize(output);
	}

	@Override
	public Wall getWall(int faceId) {
		return walls[faceId];
	}

	public AABBoid getParent() {
		return parent;
	}

	public void setParent(AABBoid parent) {
		this.parent = parent;
	}

	public Vec3 getTranslation() {
		return translation;
	}

	public void setTranslation(Vec3 translation) {
		setTranslation(translation.x, translation.y, translation.z);
	}

	public void setTranslation(float tx, float ty, float tz) {
		this.translation.set(tx, ty, tz);
	}

}
