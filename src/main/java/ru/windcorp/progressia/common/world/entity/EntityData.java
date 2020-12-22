package ru.windcorp.progressia.common.world.entity;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.Coordinates;

public class EntityData extends StatefulObject implements Collideable {
	
	private final Vec3 position = new Vec3();
	private final Vec3 velocity = new Vec3();
	
	private final Vec2 direction = new Vec2();
	
	private long entityId;
	
	private CollisionModel collisionModel = null;
	
	private double age = 0;

	public EntityData(String id) {
		super(EntityDataRegistry.getInstance(), id);
	}
	
	public Vec3 getPosition() {
		return position;
	}
	
	public Vec3i getBlockInWorld(Vec3i output) {
		if (output == null) output = new Vec3i();
		return position.round(output);
	}
	
	public Vec3i getChunkCoords(Vec3i output) {
		output = getBlockInWorld(output);
		return Coordinates.convertInWorldToChunk(output, output);
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
		output.set(
				 Math.cos(getPitch()) * Math.cos(getYaw()),
				 Math.cos(getPitch()) * Math.sin(getYaw()),
				-Math.sin(getPitch())
		);
		
		return output;
	}

}
