package ru.windcorp.progressia.common.collision;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.collision.colliders.Collider;

public interface Collideable {
	
	CollisionModel getCollisionModel();
	
	/**
	 * Invoked by {@link Collider} when two entities are about to collide.
	 * The world is at the moment of collision.
	 * @param other the colliding object
	 * @return {@code true} iff the collision should not be handled normally (e.g. this object has disappeared)
	 */
	boolean onCollision(Collideable other);
	
	/**
	 * Returns the mass of this {@link Collideable} that should be used to calculate collisions.
	 * Collision mass must be a positive number. Positive infinity is allowed.
	 * @return this object's collision mass
	 */
	float getCollisionMass();
	
	void moveAsCollideable(Vec3 displacement);
	
	void getCollideableVelocity(Vec3 output);
	void changeVelocityOnCollision(Vec3 velocityChange);

}
