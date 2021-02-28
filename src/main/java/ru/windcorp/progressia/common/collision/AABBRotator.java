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

import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;

public class AABBRotator implements AABBoid {
	
	private class AABBRotatorWall implements Wall {
		
		private final int id;

		public AABBRotatorWall(int id) {
			this.id = id;
		}

		@Override
		public void getOrigin(Vec3 output) {
			parent.getWall(id).getOrigin(output);
			AxisRotations.resolve(output, upSupplier.get(), output);
		}

		@Override
		public void getWidth(Vec3 output) {
			parent.getWall(id).getWidth(output);
			AxisRotations.resolve(output, upSupplier.get(), output);
		}

		@Override
		public void getHeight(Vec3 output) {
			parent.getWall(id).getHeight(output);
			AxisRotations.resolve(output, upSupplier.get(), output);
		}
		
	}
	
	private final Supplier<AbsFace> upSupplier;
	private final Supplier<Vec3> hingeSupplier;
	private final AABBoid parent;

	private final AABBRotatorWall[] walls = new AABBRotatorWall[AbsFace.BLOCK_FACE_COUNT];
	
	{
		for (int id = 0; id < walls.length; ++id) {
			walls[id] = new AABBRotatorWall(id);
		}
	}

	public AABBRotator(Supplier<AbsFace> upSupplier, Supplier<Vec3> hingeSupplier, AABBoid parent) {
		this.upSupplier = upSupplier;
		this.hingeSupplier = hingeSupplier;
		this.parent = parent;
	}

	@Override
	public void setOrigin(Vec3 origin) {
		Vec3 relativeOrigin = Vectors.grab3();
		Vec3 hinge = hingeSupplier.get();
		
		origin.sub(hinge, relativeOrigin);
		AxisRotations.relativize(relativeOrigin, upSupplier.get(), relativeOrigin);
		relativeOrigin.add(hinge);
		
		parent.setOrigin(relativeOrigin);
		
		Vectors.release(relativeOrigin);
	}

	@Override
	public void moveOrigin(Vec3 displacement) {
		parent.moveOrigin(displacement);
	}

	@Override
	public void getOrigin(Vec3 output) {
		parent.getOrigin(output);
		Vec3 hinge = hingeSupplier.get();
		
		output.sub(hinge);
		AxisRotations.resolve(output, upSupplier.get(), output);
		output.add(hinge);
	}

	@Override
	public void getSize(Vec3 output) {
		parent.getSize(output);
		AxisRotations.resolve(output, upSupplier.get(), output);
		output.abs();
	}

	@Override
	public Wall getWall(int faceId) {
		return walls[faceId];
	}
	
	public static CollisionModel rotate(Supplier<AbsFace> upSupplier, Supplier<Vec3> hingeSupplier, CollisionModel parent) {
		if (parent instanceof AABBoid) {
			return new AABBRotator(upSupplier, hingeSupplier, (AABBoid) parent);
		} else if (parent instanceof CompoundCollisionModel) {
			ImmutableList.Builder<CollisionModel> models = ImmutableList.builder();
			
			for (CollisionModel original : ((CompoundCollisionModel) parent).getModels()) {
				models.add(rotate(upSupplier, hingeSupplier, original));
			}
			
			return new CompoundCollisionModel(models.build());
		} else {
			throw new RuntimeException("not supported");
		}
	}

}
