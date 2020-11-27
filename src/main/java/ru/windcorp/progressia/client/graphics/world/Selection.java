package ru.windcorp.progressia.client.graphics.world;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.common.world.BlockRay;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class Selection {
	
	private final Vec3i block = new Vec3i();
	private BlockFace surface = null;
	private final Vec2 pointOnSurface = new Vec2(0.5f, 0.5f);
	private final Vec3 point = new Vec3();
	
	private boolean exists = false;
	
	private BlockRay ray = new BlockRay();
	
	public void update(WorldRender world, EntityData player) {
		Vec3 direction = new Vec3();
		Vec3 start = new Vec3();
		
		player.getLookingAtVector(direction);
		world.getEntityRenderable(player).getViewPoint(start);
		start.add(player.getPosition());
		
		exists = false;
		
		for (ray.start(start, direction); ray.getDistance() < 6; ray.next()) {
			Vec3i blockInWorld = ray.current();
			
			if (world.getData().getCollisionModelOfBlock(blockInWorld) != null) {
				exists = true;
				block.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
				ray.getPoint(point);
				surface = ray.getCurrentFace();
				// TODO selectedPointOnSurface
				break;
			}
		}
		
		ray.end();
	}
	
	public Vec3i getBlock() {
		return exists ? block : null;
	}
	
	public Vec3 getPoint() {
		return exists ? point : null;
	}
	
	public BlockFace getSurface() {
		return exists ? surface : null;
	}
	
	public Vec2 getPointOnSurface() {
		return exists ? pointOnSurface : null;
	}
	
	public boolean exists() {
		return exists;
	}

}
