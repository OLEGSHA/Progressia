package ru.windcorp.progressia.common.collision;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.world.block.BlockFace;

public interface AABBoid extends CollisionModel {
	
	void getOrigin(Vec3 output);
	void getSize(Vec3 output);
	
	default Wall getWall(BlockFace face) {
		return getWall(face.getId());
	}
	
	Wall getWall(int faceId);

}
