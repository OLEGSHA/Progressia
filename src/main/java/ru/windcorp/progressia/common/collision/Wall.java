package ru.windcorp.progressia.common.collision;

import glm.vec._3.Vec3;

public interface Wall {
	
	void getOrigin(Vec3 output);
	
	void getWidth(Vec3 output);
	void getHeight(Vec3 output);

}
