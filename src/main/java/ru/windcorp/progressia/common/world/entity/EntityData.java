package ru.windcorp.progressia.common.world.entity;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.state.StatefulObject;

public class EntityData extends StatefulObject {
	
	private final Vec3 position = new Vec3();
	private final Vec3 velocity = new Vec3();
	
	private final Vec2 direction = new Vec2();
	
	private long entityId;
	
	private double age = 0;

	public EntityData(String namespace, String name) {
		super(EntityDataRegistry.getInstance(), namespace, name);
	}
	
	public Vec3 getPosition() {
		return position;
	}
	
	public void setPosition(Vec3 position) {
		this.position.set(position);
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

}
