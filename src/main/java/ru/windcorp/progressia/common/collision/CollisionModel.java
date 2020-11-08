package ru.windcorp.progressia.common.collision;

import glm.vec._3.Vec3;

public interface CollisionModel {
	
	public void setOrigin(Vec3 origin);
	public void moveOrigin(Vec3 displacement);

}
