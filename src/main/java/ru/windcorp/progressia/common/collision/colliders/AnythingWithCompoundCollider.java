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

package ru.windcorp.progressia.common.collision.colliders;

import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.collision.CompoundCollisionModel;
import ru.windcorp.progressia.common.collision.colliders.Collider.ColliderWorkspace;
import ru.windcorp.progressia.common.collision.colliders.Collider.Collision;

class AnythingWithCompoundCollider {

	static Collider.Collision computeModelCollision(Collideable aBody, Collideable bBody, CompoundCollisionModel aModel,
			CollisionModel bModel, float tickLength, ColliderWorkspace workspace) {
		Collision result = null;

		for (CollisionModel aModelPart : aModel.getModels()) {

			Collision collision = Collider.getCollision(aBody, bBody, aModelPart, bModel, tickLength, workspace);

			// Update result
			if (collision != null) {
				Collision second;

				if (result == null || collision.time < result.time) {
					second = result;
					result = collision;
				} else {
					second = collision;
				}

				// Release Collision that is no longer used
				if (second != null)
					workspace.release(second);
			}

		}

		return result;
	}

}
