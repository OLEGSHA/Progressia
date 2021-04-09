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

package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import glm.mat._3.Mat3;
import glm.vec._3.Vec3;
import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.collision.AABBRotator;
import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.world.generic.GenericEntity;
import ru.windcorp.progressia.common.world.rels.AbsFace;

public class EntityData extends StatefulObject implements Collideable, GenericEntity {

	private final Vec3 position = new Vec3();
	private final Vec3 velocity = new Vec3();

	private final Vec3 lookingAt = new Vec3(1, 0, 0);
	private final Vec3 upVector = new Vec3(0, 0, 1);

	/**
	 * The unique {@code long} value guaranteed to never be assigned to an
	 * entity as its entity ID.
	 * This can safely be used as a placeholder or a sentinel value.
	 */
	public static final long NULL_ENTITY_ID = 0x0000_0000_0000_0000;

	private long entityId;

	private CollisionModel collisionModel = null;
	private CollisionModel rotatedCollisionModel = null;

	private double age = 0;

	public EntityData(String id) {
		super(EntityDataRegistry.getInstance(), id);
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	public void setPosition(Vec3 position) {
		move(position.sub_(getPosition()));
	}

	public void move(Vec3 displacement) {
		this.position.add(displacement);
		if (getCollisionModel() != null) {
			getCollisionModel().moveOrigin(displacement);
		}
	}

	public Vec3 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vec3 velocity) {
		this.velocity.set(velocity);
	}

	@Override
	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		if (entityId == NULL_ENTITY_ID) {
			throw new IllegalArgumentException("Attempted to set entity ID to NULL_ENTITY_ID (" + entityId + ")");
		}
		this.entityId = entityId;
	}

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public void incrementAge(double increment) {
		this.age += increment;
	}

	@Override
	public CollisionModel getCollisionModel() {
		return rotatedCollisionModel;
	}
	
	public CollisionModel getOriginalCollisionModel() {
		return collisionModel;
	}

	public void setCollisionModel(CollisionModel collisionModel) {
		this.collisionModel = collisionModel;
		this.rotatedCollisionModel = AABBRotator.rotate(this::getUpFace, this::getPosition, collisionModel);
	}

	@Override
	public boolean onCollision(Collideable other) {
		return false;
	}

	@Override
	public float getCollisionMass() {
		return 1.0f;
	}

	@Override
	public void moveAsCollideable(Vec3 displacement) {
		move(displacement);
	}

	@Override
	public void getCollideableVelocity(Vec3 output) {
		output.set(getVelocity());
	}

	@Override
	public void changeVelocityOnCollision(Vec3 velocityChange) {
		getVelocity().add(velocityChange);
	}

	public Vec3 getLookingAt() {
		return lookingAt;
	}

	public void setLookingAt(Vec3 lookingAt) {
		float lengthSq = lookingAt.x * lookingAt.x + lookingAt.y * lookingAt.y + lookingAt.z * lookingAt.z;
		if (lengthSq == 1) {
			this.lookingAt.set(lookingAt);
		} else if (lengthSq == 0) {
			throw new IllegalArgumentException("lookingAt is zero-length");
		} else if (!Float.isFinite(lengthSq)) {
			throw new IllegalArgumentException("lookingAt is not finite: " + lookingAt);
		} else {
			float length = (float) Math.sqrt(lengthSq);
			this.lookingAt.set(
				lookingAt.x / length,
				lookingAt.y / length,
				lookingAt.z / length
			);
		}
	}

	public Vec3 getUpVector() {
		return upVector;
	}
	
	public AbsFace getUpFace() {
		return AbsFace.roundToFace(getUpVector());
	}

	/**
	 * Sets this entity's up vector without updating looking at-vector.
	 * 
	 * @param upVector the Vec3 to copy up vector from
	 * @see #changeUpVector(Vec3)
	 */
	public void setUpVector(Vec3 upVector) {
		float lengthSq = upVector.x * upVector.x + upVector.y * upVector.y + upVector.z * upVector.z;
		if (lengthSq == 1) {
			this.upVector.set(upVector);
		} else if (lengthSq == 0) {
			throw new IllegalArgumentException("upVector is zero-length");
		} else if (!Float.isFinite(lengthSq)) {
			throw new IllegalArgumentException("upVector is not finite: " + upVector);
		} else {
			float length = (float) Math.sqrt(lengthSq);
			this.upVector.set(
				upVector.x / length,
				upVector.y / length,
				upVector.z / length
			);
		}
	}

	/**
	 * Computes the forward vector of this entity. An entity's forward vector is
	 * defined as a normalized projection of the looking at-vector onto the
	 * plane perpendicular to up vector, or {@code (NaN; NaN; NaN)} if looking
	 * at-vector is parallel to the up vector.
	 * 
	 * @param output a {@link Vec3} where the result is stored. May be
	 *               {@code null}.
	 * @return the computed forward vector or {@code (NaN; NaN; NaN)}
	 */
	public Vec3 getForwardVector(Vec3 output) {
		if (output == null)
			output = new Vec3();
		output.set(getUpVector()).mul(-getUpVector().dot(getLookingAt())).add(getLookingAt()).normalize();
		return output;
	}

	public double getPitch() {
		return -Math.acos(getLookingAt().dot(getUpVector())) + Math.PI / 2;
	}

	/**
	 * Updates this entity's up vector and alters looking at-vector to match the
	 * rotation of the up vector.
	 * <p>
	 * This method assumes that the up vector has changed due to rotation around
	 * some axis. The axis and the angle are computed, after which the same
	 * rotation is applied to the looking at-vector.
	 * 
	 * @param newUpVector the Vec3 to copy up vector from. May be equal to
	 *                    current up vector
	 * @see #setLookingAt(Vec3)
	 */
	public void changeUpVector(Vec3 newUpVector) {
		Objects.requireNonNull(newUpVector, "newUpVector");

		Vec3 u0 = upVector;
		Vec3 u1 = newUpVector;

		if (u1.x == 0 && u1.y == 0 && u1.z == 0) {
			// Entering weightlessness, not changing anything
			return;
		}

		if (u0.x == u1.x && u0.y == u1.y && u0.z == u1.z) {
			// Nothing changed
			return;
		}

		if (u0.x == -u1.x && u0.y == -u1.y && u0.z == -u1.z) {
			// Welp, don't do anything stupid then
			upVector.set(newUpVector);
			return;
		}

		float u1LengthSq = u1.x*u1.x + u1.y*u1.y + u1.z*u1.z;
		float u1Length = 1;
		
		if (!Float.isFinite(u1LengthSq)) {
			throw new IllegalArgumentException("newUpVector is not finite: " + newUpVector);
		} else if (u1LengthSq != 1) {
			u1Length = (float) Math.sqrt(u1LengthSq);
		}

		// u0 and u1 are now both definitely two different usable vectors
		
		if (rotateLookingAtToMatchUpVectorRotation(u0, u1, u1Length, lookingAt)) {
			return;
		}
		
		upVector.set(newUpVector).div(u1Length);
	}

	private static boolean rotateLookingAtToMatchUpVectorRotation(Vec3 u0, Vec3 u1, float u1Length, Vec3 lookingAt) {
		// Determine rotation parameters
		Vec3 axis = u0.cross_(u1);
		float cos = u0.dot(u1) / u1Length;
		float sin = axis.length() / u1Length;
		
		if (sin == 0) {
			return true;
		}
		
		axis.div(sin * u1Length); // normalize axis

		float x = axis.x;
		float y = axis.y;
		float z = axis.z;

		Mat3 matrix = Matrices.grab3();

		// Don't format. @formatter:off
		matrix.set(
			 cos + (1 - cos)*x*x,    (1 - cos)*y*x + sin*z,   (1 - cos)*z*x - sin*y,
			(1 - cos)*x*y - sin*z,    cos + (1 - cos)*y*y,    (1 - cos)*z*y + sin*x,
			(1 - cos)*x*z + sin*y,   (1 - cos)*y*z - sin*x,    cos + (1 - cos)*z*z
		);
		// @formatter:on

		matrix.mul_(lookingAt); // bug in jglm, .mul() and .mul_() are swapped

		Matrices.release(matrix);
		
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString())
			.append(" (EntityID ")
			.append(StringUtil.toFullHex(getEntityId()))
			.append(")")
			.toString();
	}

	public static String formatEntityId(long entityId) {
		return new String(StringUtil.toFullHex(entityId));
	}

	/*
	 * tmp
	 */

	@Override
	public void write(DataOutput output, IOContext context) throws IOException {
		output.writeFloat(getPosition().x);
		output.writeFloat(getPosition().y);
		output.writeFloat(getPosition().z);

		output.writeFloat(getVelocity().x);
		output.writeFloat(getVelocity().y);
		output.writeFloat(getVelocity().z);

		output.writeFloat(getLookingAt().x);
		output.writeFloat(getLookingAt().y);
		output.writeFloat(getLookingAt().z);

		output.writeFloat(getUpVector().x);
		output.writeFloat(getUpVector().y);
		output.writeFloat(getUpVector().z);

		super.write(output, context);
	}

	@Override
	public void read(DataInput input, IOContext context) throws IOException {
		Vec3 position = new Vec3(
			input.readFloat(),
			input.readFloat(),
			input.readFloat()
		);

		Vec3 velocity = new Vec3(
			input.readFloat(),
			input.readFloat(),
			input.readFloat()
		);

		Vec3 lookingAt = new Vec3(
			input.readFloat(),
			input.readFloat(),
			input.readFloat()
		);

		Vec3 upVector = new Vec3(
			input.readFloat(),
			input.readFloat(),
			input.readFloat()
		);

		setPosition(position);
		setVelocity(velocity);
		setLookingAt(lookingAt);
		setUpVector(upVector);

		super.read(input, context);
	}

}
