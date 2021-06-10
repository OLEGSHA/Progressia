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

import glm.mat._3.Mat3;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.collision.*;
import ru.windcorp.progressia.common.collision.colliders.Collider.ColliderWorkspace;
import ru.windcorp.progressia.common.collision.colliders.Collider.Collision;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.block.BlockFace;

class AABBoidCollider {

	static Collider.Collision computeModelCollision(Collideable aBody, Collideable bBody, AABBoid aModel,
			AABBoid bModel, float tickLength, ColliderWorkspace workspace) {
		Collideable obstacleBody = bBody;
		Collideable colliderBody = aBody;
		AABBoid obstacleModel = bModel;
		AABBoid colliderModel = aModel;

		Collision result = null;

		AABB originCollisionSpace = createOriginCollisionSpace(obstacleModel, colliderModel, workspace.dummyAABB);

		Vec3 collisionVelocity = Vectors.grab3();
		computeCollisionVelocity(collisionVelocity, obstacleBody, colliderBody);

		// For every wall of collision space
		for (int i = 0; i < BlockFace.BLOCK_FACE_COUNT; ++i) {
			Wall wall = originCollisionSpace.getWall(i);

			Collision collision = computeWallCollision(wall, colliderModel, collisionVelocity, tickLength, workspace,
					aBody, bBody);

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

		Vectors.release(collisionVelocity);

		return result;
	}

	private static void computeCollisionVelocity(Vec3 output, Collideable obstacleBody, Collideable colliderBody) {
		Vec3 obstacleVelocity = Vectors.grab3();
		Vec3 colliderVelocity = Vectors.grab3();

		obstacleBody.getCollideableVelocity(obstacleVelocity);
		colliderBody.getCollideableVelocity(colliderVelocity);

		output.set(colliderVelocity).sub(obstacleVelocity);

		Vectors.release(obstacleVelocity);
		Vectors.release(colliderVelocity);
	}

	private static AABB createOriginCollisionSpace(AABBoid obstacle, AABBoid collider, AABB output) {
		Vec3 obstacleOrigin = Vectors.grab3();
		Vec3 obstacleSize = Vectors.grab3();
		Vec3 colliderSize = Vectors.grab3();

		obstacle.getOrigin(obstacleOrigin);
		output.setOrigin(obstacleOrigin);

		obstacle.getSize(obstacleSize);
		collider.getSize(colliderSize);
		output.setSize(obstacleSize.add(colliderSize));

		Vectors.release(obstacleOrigin);
		Vectors.release(obstacleSize);
		Vectors.release(colliderSize);

		return output;
	}

	/*
	 * Here we determine whether a collision has actually happened, and if it
	 * did, at what moment. The basic idea is to compute the moment of collision
	 * and impact coordinates in wall coordinate space. Then, we can check
	 * impact coordinates to determine if we actually hit the wall or flew by
	 * and then check time to make sure the collision is not too far in the
	 * future and not in the past. DETAILED EXPLANATION: Consider a surface
	 * defined by an origin r_wall and two noncollinear nonzero vectors w and h.
	 * Consider a line defined by an origin r_line and a nonzero vector v. Then,
	 * a collision occurs if there exist x, y and t such that ______ _ r_line +
	 * v * t and ______ _ _ r_wall + w * x + h * y describe the same location
	 * (indeed, this corresponds to a collision at moment t0 + t with a point on
	 * the wall with coordinates (x; y) in (w; h) coordinate system). Therefore,
	 * ______ _ ______ _ _ r_line + v*t = r_wall + w*x + h*y; _ ⎡w_x h_x
	 * -v_x⎤ ⎡x⎤ _ ______ ______ r = ⎢w_y h_y -v_y⎥ * ⎢y⎥, where r
	 * = r_line - r_wall; ⎣w_z h_z -v_z⎦ ⎣t⎦ ⎡x⎤ ⎡w_x h_x -v_x⎤
	 * -1 _ ⎢y⎥ = ⎢w_y h_y -v_y⎥ * r, if the matrix is invertible.
	 * ⎣t⎦ ⎣w_z h_z -v_z⎦ Then, one only needs to ensure that: 0 < x <
	 * 1, 0 < y < 1, and 0 < t < T, where T is remaining tick time. If the
	 * matrix is not invertible or any of the conditions are not met, no
	 * collision happened. If all conditions are satisfied, then the moment of
	 * impact is t0 + t.
	 */
	private static Collision computeWallCollision(Wall obstacleWall, AABBoid colliderModel, Vec3 collisionVelocity,
			float tickLength, ColliderWorkspace workspace, Collideable aBody, Collideable bBody) {
		Vec3 w = Vectors.grab3();
		Vec3 h = Vectors.grab3();
		Vec3 v = Vectors.grab3();
		Mat3 m = Matrices.grab3(); // The matrix [w h -v]
		Vec3 r = Vectors.grab3();
		Vec3 r_line = Vectors.grab3();
		Vec3 r_wall = Vectors.grab3();
		Vec3 xyt = Vectors.grab3();

		try {
			obstacleWall.getWidth(w);
			obstacleWall.getHeight(h);

			v.set(collisionVelocity);

			if (isExiting(v, w, h)) {
				return null;
			}

			obstacleWall.getOrigin(r_wall);
			colliderModel.getOrigin(r_line);
			r.set(r_line).sub(r_wall);
			m.c0(w).c1(h).c2(v.negate());

			if (Math.abs(m.det()) < 1e-6) {
				return null;
			}

			m.inverse().mul(r, xyt);

			float x = xyt.x;
			float y = xyt.y;
			float t = xyt.z;

			if (x <= 0 || x >= 1 || y <= 0 || y >= 1) {
				// We're missing the wall
				return null;
			}

			if (t < 0 || t > tickLength) {
				// We're colliding at the wrong moment
				return null;
			}

			return workspace.grab().set(aBody, bBody, obstacleWall, t);
		} finally {
			Vectors.release(w);
			Vectors.release(h);
			Vectors.release(v);
			Matrices.release(m);
			Vectors.release(r);
			Vectors.release(r_line);
			Vectors.release(r_wall);
			Vectors.release(xyt);
		}
	}

	private static boolean isExiting(Vec3 v, Vec3 w, Vec3 h) {
		Vec3 tmp = Vectors.grab3().set(w).cross(h);
		boolean result = Vec3.dot(tmp, v) >= 0;
		Vectors.release(tmp);
		return result;
	}

	private AABBoidCollider() {
	}

}
