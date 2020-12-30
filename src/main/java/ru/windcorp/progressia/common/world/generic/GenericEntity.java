package ru.windcorp.progressia.common.world.generic;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;

public interface GenericEntity {
	
	String getId();
	Vec3 getPosition();
	
	default Vec3i getBlockInWorld(Vec3i output) {
		if (output == null) output = new Vec3i();
		return getPosition().round(output);
	}
	
	default Vec3i getChunkCoords(Vec3i output) {
		output = getBlockInWorld(output);
		return Coordinates.convertInWorldToChunk(output, output);
	}

}
