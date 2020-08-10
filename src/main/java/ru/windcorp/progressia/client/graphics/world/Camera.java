/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics.world;

import static java.lang.Math.*;

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;

public class Camera {
	
	private final Vec3 position = new Vec3();
	
	private float pitch;
	private float yaw;
	
	private float fieldOfView;
	
	public Camera(Vec3 position, float pitch, float yaw, float fieldOfView) {
		teleport(position);
		setPitch(pitch);
		setYaw(yaw);
		setFieldOfView(fieldOfView);
	}
	
	public Camera() {}

	public void apply(WorldRenderHelper helper) {
		Mat4 previous = helper.getViewTransform();
		Glm.perspective(
				computeFovY(),
				GraphicsInterface.getAspectRatio(),
				0.01f, 10000.0f,
				helper.pushViewTransform()
		).mul(previous);
		
		helper.pushViewTransform().rotateX(pitch).rotateY(yaw);
		
		helper.pushViewTransform().translate(position.negate());
		position.negate();
	}
	
	private float computeFovY() {
		float widthOverHeight = GraphicsInterface.getAspectRatio();
		
		if (widthOverHeight >= 1) {
			return fieldOfView;
		} else {
			return (float) (2 * atan(
					1 / widthOverHeight
					*
					tan(fieldOfView / 2)
			));
		}
	}

	public Vec3 getPosition() {
		return position;
	}
	
	public void teleport(Vec3 pos) {
		position.set(pos);
	}
	
	public void move(Vec3 pos) {
		position.add(pos);
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		final float maxPitch = (float) (Math.PI / 2);
		this.pitch = Glm.clamp(pitch, -maxPitch, +maxPitch);
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = Glm.mod(yaw, 2 * (float) PI);
	}
	
	public void setDirection(float pitch, float yaw) {
		setPitch(pitch);
		setYaw(yaw);
	}
	
	public void turn(float pitchChange, float yawChange) {
		setPitch(getPitch() + pitchChange);
		setYaw(getYaw() + yawChange);
	}
	
	public float getFieldOfView() {
		return fieldOfView;
	}
	
	public void setFieldOfView(float fieldOfView) {
		this.fieldOfView = fieldOfView;
	}

}
