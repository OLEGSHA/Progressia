package ru.windcorp.progressia.common.collision;

import java.util.Collection;
import java.util.Map;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class AABB implements CollisionModel {

	private final Map<BlockFace, CollisionWall> faces = BlockFace.mapToFaces(
			new CollisionWall(-0.5f, -0.5f, -0.5f,   +1,  0,  0,    0,  0, +1),
			new CollisionWall(+0.5f, -0.5f, -0.5f,    0, +1,  0,    0,  0, +1),
			new CollisionWall(+0.5f, +0.5f, -0.5f,   -1,  0,  0,    0,  0, +1),
			new CollisionWall(-0.5f, +0.5f, -0.5f,    0, -1,  0,    0,  0, +1),
			
			new CollisionWall(-0.5f, -0.5f, +0.5f,   +1,  0,  0,    0, +1,  0),
			new CollisionWall(-0.5f, -0.5f, -0.5f,    0, +1,  0,   +1,  0,  0)
	);
	
	private final Vec3 origin = new Vec3();
	private final Vec3 size = new Vec3();
	
	public AABB(Vec3 origin, Vec3 size) {
		this.origin.set(origin);
		this.size.set(size);
		
		for (CollisionWall wall : getFaces()) {
			wall.moveOrigin(origin);
			wall.getWidth().mul(size);
			wall.getHeight().mul(size);
		}
	}
	
	public AABB(
			float ox, float oy, float oz,
			float xSize, float ySize, float zSize
	) {
		this.origin.set(ox, oy, oz);
		this.size.set(xSize, ySize, zSize);
		
		for (CollisionWall wall : getFaces()) {
			wall.moveOrigin(ox, oy, oz);
			wall.getWidth().mul(xSize, ySize, zSize);
			wall.getHeight().mul(xSize, ySize, zSize);
		}
	}
	
	public Collection<CollisionWall> getFaces() {
		return faces.values();
	}
	
	public Vec3 getOrigin() {
		return origin;
	}
	
	@Override
	public void setOrigin(Vec3 origin) {
		for (CollisionWall wall : getFaces()) {
			wall.getOrigin().sub(this.origin).add(origin);
		}

		this.origin.set(origin);
	}
	
	@Override
	public void moveOrigin(Vec3 displacement) {
		for (CollisionWall wall : getFaces()) {
			wall.getOrigin().add(displacement);
		}

		this.origin.add(displacement);
	}
	
	public Vec3 getSize() {
		return size;
	}
	
	public void setSize(Vec3 size) {
		setSize(size.x, size.y, size.z);
	}
	
	public void setSize(float xSize, float ySize, float zSize) {
		for (CollisionWall wall : getFaces()) {
			wall.getWidth().div(this.size).mul(xSize, ySize, zSize);
			wall.getHeight().div(this.size).mul(xSize, ySize, zSize);
			wall.getOrigin().sub(getOrigin()).div(this.size).mul(xSize, ySize, zSize).add(getOrigin());
		}
		
		this.size.set(xSize, ySize, zSize);
	}
	
}
