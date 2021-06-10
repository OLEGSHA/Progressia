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

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.collision.*;
import ru.windcorp.progressia.common.util.LowOverheadCache;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.WorldData;

public class Collider {

	private static final int MAX_COLLISIONS_PER_ENTITY = 64;

	/**
	 * Dear Princess Celestia,
	 * <p>
	 * When {@linkplain #advanceTime(Collection, Collision, WorldData, float)
	 * advancing time}, time step for all entities <em>except</em> currently
	 * colliding bodies is the current collisions's timestamp relative to now.
	 * However, currently colliding bodies (Collision.a and Collision.b) have a
	 * smaller time step. This is done to make sure they don't intersect due to
	 * rounding errors.
	 * <p>
	 * Today I learned that bad code has nothing to do with friendship, although
	 * lemme tell ya: it's got some dank magic.
	 * <p>
	 * Your faithful student,<br />
	 * Kostyl.
	 */
	private static final float TIME_STEP_COEFFICIENT_FOR_CURRENTLY_COLLIDING_BODIES = 1f/*
																						 * 1e
																						 * -
																						 * 1f
																						 */;

	public static void performCollisions(List<? extends Collideable> colls, WorldData world, float tickLength,
			ColliderWorkspace workspace) {
		int collisionCount = 0;
		int maxCollisions = colls.size() * MAX_COLLISIONS_PER_ENTITY;

		while (true) {
			if (collisionCount > maxCollisions) {
				LogManager.getLogger().warn("Attempted to handle more than {} collisions", maxCollisions);
				return;
			}

			Collision firstCollision = getFirstCollision(colls, tickLength, world, workspace);

			if (firstCollision == null) {
				break;
			} else {
				collide(firstCollision, colls, world, tickLength, workspace);
				workspace.release(firstCollision);
				collisionCount++;

				tickLength -= firstCollision.time;
			}
		}

		advanceTime(colls, null, world, tickLength);
	}

	private static Collision getFirstCollision(List<? extends Collideable> colls, float tickLength, WorldData world,
			ColliderWorkspace workspace) {
		Collision result = null;
		Collideable worldColl = workspace.worldCollisionHelper.getCollideable();

		// For every pair of colls
		for (int i = 0; i < colls.size(); ++i) {
			Collideable a = colls.get(i);

			tuneWorldCollisionHelper(a, tickLength, world, workspace);

			result = workspace.updateLatestCollision(result, getCollision(a, worldColl, tickLength, workspace));

			for (int j = i + 1; j < colls.size(); ++j) {
				Collideable b = colls.get(j);
				Collision collision = getCollision(a, b, tickLength, workspace);
				result = workspace.updateLatestCollision(result, collision);
			}
		}

		return result;
	}

	private static void tuneWorldCollisionHelper(Collideable coll, float tickLength, WorldData world,
			ColliderWorkspace workspace) {
		WorldCollisionHelper wch = workspace.worldCollisionHelper;
		wch.tuneToCollideable(world, coll, tickLength);
	}

	static Collision getCollision(Collideable a, Collideable b, float tickLength, ColliderWorkspace workspace) {
		CollisionModel aModel = a.getCollisionModel();
		CollisionModel bModel = b.getCollisionModel();
		return getCollision(a, b, aModel, bModel, tickLength, workspace);
	}

	static Collision getCollision(Collideable aBody, Collideable bBody, CollisionModel aModel, CollisionModel bModel,
			float tickLength, ColliderWorkspace workspace) {
		if (aModel instanceof AABBoid && bModel instanceof AABBoid) {
			return AABBoidCollider.computeModelCollision(aBody, bBody, (AABBoid) aModel, (AABBoid) bModel, tickLength,
					workspace);
		}

		if (aModel instanceof CompoundCollisionModel) {
			return AnythingWithCompoundCollider.computeModelCollision(aBody, bBody, (CompoundCollisionModel) aModel,
					bModel, tickLength, workspace);
		}

		if (bModel instanceof CompoundCollisionModel) {
			return AnythingWithCompoundCollider.computeModelCollision(bBody, aBody, (CompoundCollisionModel) bModel,
					aModel, tickLength, workspace);
		}

		throw new UnsupportedOperationException(
				"Collisions between " + aModel + " and " + bModel + " are not yet implemented");
	}

	private static void collide(Collision collision,

			Collection<? extends Collideable> colls, WorldData world, float tickLength, ColliderWorkspace workspace) {
		advanceTime(colls, collision, world, collision.time);

		boolean doNotHandle = false;

		doNotHandle |= collision.a.onCollision(collision.b);
		doNotHandle |= collision.b.onCollision(collision.a);

		if (doNotHandle) {
			return;
		}

		handlePhysics(collision);
	}

	/*
	 * Here we compute the change in body velocities due to a collision. We make
	 * the following simplifications: 1) The bodies are perfectly rigid; 2) The
	 * collision is perfectly inelastic (no bouncing); 3) The bodies are
	 * spherical; 4) No tangential friction exists (bodies do not experience
	 * friction when sliding against each other); 5) Velocities are not
	 * relativistic. Angular momentum is ignored per 3) and 4), e.g. when
	 * something pushes an end of a long stick, the stick does not rotate.
	 * DETAILED EXPLANATION: Two spherical (sic) bodies, a and b, experience a
	 * perfectly inelastic collision along a unit vector _ _ _ _ _ n = (w ⨯ h)
	 * / (|w ⨯ h|), _ _ where w and h are two noncollinear nonzero vectors on
	 * the dividing plane. ___ ___ Body masses and velocities are M_a, M_b and
	 * v_a, v_b, respectively. ___ ___ After the collision desired velocities
	 * are u_a and u_b, respectively. _ (Notation convention: suffix 'n' denotes
	 * a vector projection onto vector n, and suffix 't' denotes a vector
	 * projection onto the dividing plane.) Consider the law of conservation of
	 * momentum for axis n and the dividing plane: ____________ ____________
	 * ________________ n: ⎧ p_a_before_n + p_b_before_n = p_common_after_n;
	 * ⎨ ___________ ____________ t: ⎩ p_i_after_t = p_i_before_t for any i
	 * in {a, b}. Expressing all p_* in given terms: ___ _ ___ _ ___ ___ ____
	 * ____ n: ⎧ M_a * (v_a ⋅ n) + M_b * (v_b ⋅ n) = (M_a + M_b) * u_n,
	 * where u_n ≡ u_an = u_bn; ⎨ ____ ___ _ ___ _ t: ⎩ u_it = v_i - n *
	 * (v_i ⋅ n) for any i in {a, b}. Therefore: ___ _ ___ _ ___ _ u_n = n * (
	 * M_a/(M_a + M_b) * v_a ⋅ n + M_b/(M_a + M_b) * v_b ⋅ n ); or,
	 * equivalently, ___ _ ___ _ ___ _ u_n = n * ( m_a * v_a ⋅ n + m_b * v_b
	 * ⋅ n ), where m_a and m_b are relative masses (see below). Finally, ___
	 * ____ ___ u_i = u_it + u_n for any i in {a, b}. The usage of relative
	 * masses m_i permits a convenient generalization of the algorithm for
	 * infinite masses, signifying masses "significantly greater" than finite
	 * masses: 1) If both M_a and M_b are finite, let m_i = M_i / (M_a + M_b)
	 * for any i in {a, b}. 2) If M_i is finite but M_j is infinite, let m_i = 0
	 * and m_j = 1. 3) If both M_a and M_b are infinite, let m_i = 1/2 for any i
	 * in {a, b}.
	 */
	private static void handlePhysics(Collision collision) {
		// Fuck JGLM
		Vec3 n = Vectors.grab3();
		Vec3 v_a = Vectors.grab3();
		Vec3 v_b = Vectors.grab3();
		Vec3 u_n = Vectors.grab3();
		Vec3 u_at = Vectors.grab3();
		Vec3 u_bt = Vectors.grab3();
		Vec3 du_a = Vectors.grab3();
		Vec3 du_b = Vectors.grab3();

		n.set(collision.wallWidth).cross(collision.wallHeight).normalize();
		collision.a.getCollideableVelocity(v_a);
		collision.b.getCollideableVelocity(v_b);

		float M_a = collision.a.getCollisionMass();
		float M_b = collision.b.getCollisionMass();

		float m_a, m_b;

		if (Float.isFinite(M_a)) {
			if (Float.isFinite(M_b)) {
				float sum = M_a + M_b;
				m_a = M_a / sum;
				m_b = M_b / sum;
			} else {
				m_a = 0;
				m_b = 1;
			}
		} else {
			if (Float.isFinite(M_b)) {
				m_a = 1;
				m_b = 0;
			} else {
				m_a = 0.5f;
				m_b = 0.5f;
			}
		}

		u_n.set(n).mul(m_a * Vec3.dot(v_a, n) + m_b * Vec3.dot(v_b, n));
		u_at.set(n).mul(Vec3.dot(v_a, n)).negate().add(v_a);
		u_bt.set(n).mul(Vec3.dot(v_b, n)).negate().add(v_b);
		du_a.set(u_n).add(u_at).sub(v_a);
		du_b.set(u_n).add(u_bt).sub(v_b);

		collision.a.changeVelocityOnCollision(du_a);
		collision.b.changeVelocityOnCollision(du_b);

		separate(collision, n, m_a, m_b);

		// JGML is still to fuck
		Vectors.release(n);
		Vectors.release(v_a);
		Vectors.release(v_b);
		Vectors.release(u_n);
		Vectors.release(u_at);
		Vectors.release(u_bt);
		Vectors.release(du_a);
		Vectors.release(du_b);
	}

	private static void separate(Collision collision, Vec3 normal, float aRelativeMass, float bRelativeMass) {
		final float margin = 1e-4f;

		Vec3 displacement = Vectors.grab3();

		displacement.set(normal).mul(margin).mul(bRelativeMass);
		collision.a.moveAsCollideable(displacement);

		displacement.set(normal).mul(margin).mul(aRelativeMass).negate();
		collision.b.moveAsCollideable(displacement);

		Vectors.release(displacement);
	}

	private static void advanceTime(Collection<? extends Collideable> colls, Collision exceptions, WorldData world,
			float step) {
		world.advanceTime(step);

		Vec3 tmp = Vectors.grab3();

		for (Collideable coll : colls) {
			coll.getCollideableVelocity(tmp);

			float currentStep = step;

			if (exceptions != null && (exceptions.a == coll || exceptions.b == coll)) {
				currentStep *= TIME_STEP_COEFFICIENT_FOR_CURRENTLY_COLLIDING_BODIES;
			}

			tmp.mul(currentStep);
			coll.moveAsCollideable(tmp);
		}

		Vectors.release(tmp);
	}

	public static class ColliderWorkspace {

		private final LowOverheadCache<Collision> collisionCache = new LowOverheadCache<>(Collision::new);

		AABB dummyAABB = new AABB(0, 0, 0, 1, 1, 1);

		WorldCollisionHelper worldCollisionHelper = new WorldCollisionHelper();

		Collision grab() {
			return collisionCache.grab();
		}

		void release(Collision object) {
			collisionCache.release(object);
		}

		Collision updateLatestCollision(Collision a, Collision b) {
			if (a == null) {
				return b; // may be null
			} else if (b == null) {
				return a;
			}

			Collision first, second;

			if (a.time > b.time) {
				first = b;
				second = a;
			} else {
				first = a;
				second = b;
			}

			release(second);
			return first;
		}

	}

	static class Collision {
		public Collideable a;
		public Collideable b;

		public final Vec3 wallWidth = new Vec3();
		public final Vec3 wallHeight = new Vec3();

		/**
		 * Time offset from the start of the tick. 0 means right now, tickLength
		 * means at the end of the tick.
		 */
		public float time;

		public Collision set(Collideable a, Collideable b, Wall wall, float time) {
			this.a = a;
			this.b = b;
			wall.getWidth(wallWidth);
			wall.getHeight(wallHeight);
			this.time = time;

			return this;
		}
	}

	private Collider() {
	}

}
