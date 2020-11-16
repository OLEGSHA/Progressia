package ru.windcorp.progressia.client.graphics.world;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.BlockRay;
import ru.windcorp.progressia.common.world.Player;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class LocalPlayer extends Player {

	private Vec3i lookingAt = new Vec3i();
	private boolean isLookingAtBlock = false;
	
	private BlockRay lookingAtRay = new BlockRay();
	
	public LocalPlayer(EntityData entity) {
		super(entity);
	}
	
	public Vec3i getLookingAt() {
		return isLookingAtBlock ? lookingAt : null;
	}
	
	public void update(WorldRender world) {
		updateLookingAt(world);
	}
	
	private void updateLookingAt(WorldRender world) {
		Vec3 direction = Vectors.grab3();
		Vec3 start = Vectors.grab3();
		
		BlockRay ray = lookingAtRay;
		EntityData player = getEntity();
		
		player.getLookingAtVector(direction);
		world.getEntityRenderable(player).getViewPoint(start);
		start.add(player.getPosition());
		
		isLookingAtBlock = false;
		
		for (ray.start(start, direction); ray.getDistance() < 6; ray.next()) {
			Vec3i blockInWorld = ray.current();
			
			if (world.getData().getCollisionModelOfBlock(blockInWorld) != null) {
				isLookingAtBlock = true;
				lookingAt.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
				break;
			}
		}
		
		ray.end();
		
		Vectors.release(direction);
		Vectors.release(start);
	}

	public EntityRenderable getRenderable(WorldRender world) {
		return world.getEntityRenderable(getEntity());
	}

}
