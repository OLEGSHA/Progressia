package ru.windcorp.progressia.common.collision;

import glm.vec._3.Vec3;

public class CollisionWall {
	
	private final Vec3 origin = new Vec3();
	private final Vec3 width = new Vec3();
	private final Vec3 height = new Vec3();
	
	public CollisionWall(Vec3 origin, Vec3 width, Vec3 height) {
		this.origin.set(origin);
		this.width.set(width);
		this.height.set(height);
	}
	
	public CollisionWall(
		float ox, float oy, float oz,
		float wx, float wy, float wz,
		float hx, float hy, float hz
	) {
		this.origin.set(ox, oy, oz);
		this.width.set(wx, wy, wz);
		this.height.set(hx, hy, hz);
	}
	
	public Vec3 getOrigin() {
		return origin;
	}
	
	public Vec3 getWidth() {
		return width;
	}
	
	public Vec3 getHeight() {
		return height;
	}
	
	public void setOrigin(Vec3 origin) {
		setOrigin(origin.x, origin.y, origin.z);
	}
	
	public void setOrigin(float x, float y, float z) {
		this.origin.set(x, y, z);
	}
	
	public void moveOrigin(Vec3 displacement) {
		moveOrigin(displacement.x, displacement.y, displacement.z);
	}
	
	public void moveOrigin(float dx, float dy, float dz) {
		this.origin.add(dx, dy, dz);
	}

}
