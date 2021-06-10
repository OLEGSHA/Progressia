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

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.generic.GenericEntity;

public class EntityData extends StatefulObject implements Collideable, GenericEntity {

	private final Vec3 position = new Vec3();
	private final Vec3 velocity = new Vec3();

	private final Vec2 direction = new Vec2();

	/**
	 * The unique {@code long} value guaranteed to never be assigned to an
	 * entity as its entity ID. This can safely be used as a placeholder or a
	 * sentinel value.
	 */
	public static final long NULL_ENTITY_ID = 0x0000_0000_0000_0000;

	private long entityId;

	private CollisionModel collisionModel = null;

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

	public Vec2 getDirection() {
		return direction;
	}

	public void setDirection(Vec2 direction) {
		this.direction.set(direction.x, direction.y);
	}

	public float getYaw() {
		return getDirection().x;
	}

	public float getPitch() {
		return getDirection().y;
	}

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
		return collisionModel;
	}

	public void setCollisionModel(CollisionModel collisionModel) {
		this.collisionModel = collisionModel;
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

	public Vec3 getLookingAtVector(Vec3 output) {
		output.set(Math.cos(getPitch()) * Math.cos(getYaw()), Math.cos(getPitch()) * Math.sin(getYaw()),
				-Math.sin(getPitch()));

		return output;
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString()).append(" (EntityID ").append(StringUtil.toFullHex(getEntityId()))
				.append(")").toString();
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

		output.writeFloat(getDirection().x);
		output.writeFloat(getDirection().y);

		super.write(output, context);
	}

	@Override
	public void read(DataInput input, IOContext context) throws IOException {
		Vec3 position = new Vec3(input.readFloat(), input.readFloat(), input.readFloat());

		Vec3 velocity = new Vec3(input.readFloat(), input.readFloat(), input.readFloat());

		Vec2 direction = new Vec2(input.readFloat(), input.readFloat());

		setPosition(position);
		setVelocity(velocity);
		setDirection(direction);

		super.read(input, context);
	}

}
