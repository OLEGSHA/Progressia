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

import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;

import static java.lang.Math.*;

public class CollisionPathComputer {

	private static final float PADDING = 0.5f;

	public static void forEveryBlockInCollisionPath(Collideable coll, float maxTime, Consumer<Vec3i> action) {
		Vec3 displacement = Vectors.grab3();
		coll.getCollideableVelocity(displacement);
		displacement.mul(maxTime);

		handleModel(coll.getCollisionModel(), displacement, action);

		Vectors.release(displacement);
	}

	private static void handleModel(CollisionModel model, Vec3 displacement, Consumer<Vec3i> action) {
		if (model instanceof CompoundCollisionModel) {
			for (CollisionModel subModel : ((CompoundCollisionModel) model).getModels()) {
				handleModel(subModel, displacement, action);
			}
		} else if (model instanceof AABBoid) {
			handleAABBoid((AABBoid) model, displacement, action);
		} else {
			throw new RuntimeException("not supported");
		}
	}

	private static void handleAABBoid(AABBoid model, Vec3 displacement, Consumer<Vec3i> action) {
		Vec3 size = Vectors.grab3();
		Vec3 origin = Vectors.grab3();

		model.getOrigin(origin);
		model.getSize(size);

		origin.mul(2).sub(size).div(2); // Subtract 0.5*size

		Vec3i pos = Vectors.grab3i();

		for (pos.x = (int) floor(origin.x + min(0, size.x) + min(0, displacement.x) - PADDING); pos.x <= (int) ceil(
				origin.x + max(0, size.x) + max(0, displacement.x) + PADDING); pos.x += 1) {
			for (pos.y = (int) floor(origin.y + min(0, size.y) + min(0, displacement.y) - PADDING); pos.y <= (int) ceil(
					origin.y + max(0, size.y) + max(0, displacement.y) + PADDING); pos.y += 1) {
				for (pos.z = (int) floor(
						origin.z + min(0, size.z) + min(0, displacement.z) - PADDING); pos.z <= (int) ceil(
								origin.z + max(0, size.z) + max(0, displacement.z) + PADDING); pos.z += 1) {
					action.accept(pos);
				}
			}
		}

		Vectors.release(origin);
		Vectors.release(size);
		Vectors.release(pos);
	}

}
