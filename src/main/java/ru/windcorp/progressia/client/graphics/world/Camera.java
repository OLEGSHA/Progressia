/*******************************************************************************
 * Progressia
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

import java.util.Collection;
import java.util.function.Consumer;

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.world.Camera.Anchor.Mode;
import ru.windcorp.progressia.common.util.Vectors;

public class Camera {
	
	public static interface Anchor {
		
		/**
		 * Offset is applied after the rotation.
		 */
		public static interface Mode {
			void getCameraOffset(Vec3 output);
			void applyCameraRotation(Mat4 output);
			
			public static Mode of(
					Consumer<Vec3> offsetGetter,
					Consumer<Mat4> rotator
			) {
				return new Mode() {
					@Override
					public void getCameraOffset(Vec3 output) {
						offsetGetter.accept(output);
					}
					
					@Override
					public void applyCameraRotation(Mat4 output) {
						rotator.accept(output);
					}
				};
			}
		}
		
		void getCameraPosition(Vec3 output);
		void getCameraVelocity(Vec3 output);
		float getCameraYaw();
		float getCameraPitch();
		
		Collection<Mode> getCameraModes();
		
	}
	
	private Anchor anchor;
	
	private Anchor.Mode[] modes;
	private int currentModeIndex;
	
	private float fieldOfView;
	
	public Camera(float fieldOfView) {
		setFieldOfView(fieldOfView);
	}
	
	public Camera() {}

	public void apply(WorldRenderHelper helper) {
		applyPerspective(helper);
		rotateCoordinateSystem(helper);
		
		applyMode(helper);
		applyDirection(helper);
		applyPosition(helper);
	}

	private void applyPerspective(WorldRenderHelper helper) {
		Mat4 previous = helper.getViewTransform();
		
		Glm.perspective(
				computeFovY(),
				GraphicsInterface.getAspectRatio(),
				0.01f, 10000.0f,
				helper.pushViewTransform()
		).mul(previous);
	}

	private void rotateCoordinateSystem(WorldRenderHelper helper) {
		helper.pushViewTransform().rotateX(-PI / 2).rotateZ(PI / 2);
	}

	private void applyMode(WorldRenderHelper helper) {
		Mode mode = getMode();
		
		Mat4 matrix = helper.pushViewTransform();
		
		Vec3 offset = Vectors.grab3();
		mode.getCameraOffset(offset);
		
		offset.negate();
		matrix.translate(offset);
		
		Vectors.release(offset);
		
		mode.applyCameraRotation(matrix);
	}
	
	private void applyDirection(WorldRenderHelper helper) {
		helper.pushViewTransform()
		.rotateY(-anchor.getCameraPitch())
		.rotateZ(-anchor.getCameraYaw());
	}

	private void applyPosition(WorldRenderHelper helper) {
		Vec3 v = Vectors.grab3();
		
		anchor.getCameraPosition(v);
		v.negate();
		
		helper.pushViewTransform().translate(v);
		
		Vectors.release(v);
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
	
	public float getFieldOfView() {
		return fieldOfView;
	}
	
	public void setFieldOfView(float fieldOfView) {
		this.fieldOfView = fieldOfView;
	}
	
	public Anchor getAnchor() {
		return anchor;
	}
	
	public boolean hasAnchor() {
		return anchor != null;
	}
	
	public void setAnchor(Anchor anchor) {
		if (anchor == null) {
			this.anchor = null;
			this.modes = null;
			return;
		}
		
		Collection<Mode> modesCollection = anchor.getCameraModes();
		
		if (modesCollection.isEmpty()) {
			throw new IllegalArgumentException(
					"Anchor " + anchor + " returned no camera modes,"
							+ " at least one required"
			);
		}
		
		this.anchor = anchor;
		
		this.modes = modesCollection.toArray(new Mode[modesCollection.size()]);
		this.currentModeIndex = 0;
	}
	
	public Anchor.Mode getMode() {
		return modes[currentModeIndex];
	}
	
	public void selectNextMode() {
		if (currentModeIndex == modes.length - 1) {
			currentModeIndex = 0;
		} else {
			currentModeIndex++;
		}
	}

}
