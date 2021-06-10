/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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
 */

package ru.windcorp.progressia.client.world.entity;

import static java.lang.Math.atan2;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;
import static ru.windcorp.progressia.common.util.FloatMathUtil.normalizeAngle;

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.entity.EntityData;

public abstract class NPedModel extends EntityRenderable {

	protected static abstract class BodyPart {
		private final Renderable renderable;
		private final Vec3 translation = new Vec3();

		public BodyPart(Renderable renderable, Vec3 joint) {
			this.renderable = renderable;
			if (joint != null) {
				this.translation.set(joint);
			}
		}

		protected void render(ShapeRenderHelper renderer, NPedModel model) {
			renderer.pushTransform().translate(translation);
			applyTransform(renderer.pushTransform(), model);
			renderable.render(renderer);
			renderer.popTransform();
			renderer.popTransform();
		}

		protected abstract void applyTransform(Mat4 mat, NPedModel model);

		public Vec3 getTranslation() {
			return translation;
		}
	}

	public static class Body extends BodyPart {
		public Body(Renderable renderable) {
			super(renderable, null);
		}

		@Override
		protected void applyTransform(Mat4 mat, NPedModel model) {
			// Do nothing
		}
	}

	public static class Head extends BodyPart {
		private final float maxYaw;
		private final float maxPitch;

		private final Vec3 viewPoint;

		public Head(Renderable renderable, Vec3 joint, double maxYawDegrees, double maxPitchDegrees, Vec3 viewPoint) {
			super(renderable, joint);
			this.maxYaw = (float) toRadians(maxYawDegrees);
			this.maxPitch = (float) toRadians(maxPitchDegrees);
			this.viewPoint = viewPoint;
		}

		@Override
		protected void applyTransform(Mat4 mat, NPedModel model) {
			mat.rotateZ(model.getHeadYaw()).rotateY(model.getHeadPitch());
		}

		public Vec3 getViewPoint() {
			return viewPoint;
		}
	}

	protected final Body body;
	protected final Head head;

	private float walkingParameter = 0;
	private float velocityParameter = 0;
	private float velocity = 0;

	/**
	 * If {@link #velocity} is greater than this value,
	 * {@link #velocityParameter} is 1.0.
	 */
	private float maxEffectiveVelocity = 5 * Units.METERS_PER_SECOND;

	/**
	 * If {@link #velocity} is less than {@link #maxEffectiveVelocity}, then
	 * {@code velocityCoeff = exp(velocity / maxEffectiveVelocity, velocityCoeffPower)}.
	 */
	private float velocityCoeffPower = 1;

	private final float scale;

	private float walkingFrequency;

	private float bodyYaw = Float.NaN;
	private float headYaw;
	private float headPitch;

	public NPedModel(EntityData data, Body body, Head head, float scale) {
		super(data);
		this.body = body;
		this.head = head;
		this.scale = scale;

		evaluateAngles();
	}

	@Override
	public void render(ShapeRenderHelper renderer) {
		renderer.pushTransform().scale(scale).rotateZ(bodyYaw);
		renderBodyParts(renderer);
		renderer.popTransform();

		accountForVelocity();
		evaluateAngles();
	}

	protected void renderBodyParts(ShapeRenderHelper renderer) {
		body.render(renderer, this);
		head.render(renderer, this);
	}

	private void evaluateAngles() {
		float globalYaw = normalizeAngle(getData().getYaw());

		if (Float.isNaN(bodyYaw)) {
			bodyYaw = globalYaw;
			headYaw = 0;
		} else {
			headYaw = normalizeAngle(globalYaw - bodyYaw);

			if (headYaw > +head.maxYaw) {
				bodyYaw += headYaw - +head.maxYaw;
				headYaw = +head.maxYaw;
			} else if (headYaw < -head.maxYaw) {
				bodyYaw += headYaw - -head.maxYaw;
				headYaw = -head.maxYaw;
			}
		}

		bodyYaw = normalizeAngle(bodyYaw);

		headPitch = Glm.clamp(getData().getPitch(), -head.maxPitch, head.maxPitch);
	}

	private void accountForVelocity() {
		Vec3 horizontal = new Vec3(getData().getVelocity());
		horizontal.z = 0;

		velocity = horizontal.length();

		evaluateVelocityCoeff();

		// TODO switch to world time
		walkingParameter += velocity * GraphicsInterface.getFrameLength() * 1000;

		bodyYaw += velocityParameter * normalizeAngle((float) (atan2(horizontal.y, horizontal.x) - bodyYaw))
				* min(1, GraphicsInterface.getFrameLength() * 10);
	}

	private void evaluateVelocityCoeff() {
		if (velocity > maxEffectiveVelocity) {
			velocityParameter = 1;
		} else {
			velocityParameter = (float) pow(velocity / maxEffectiveVelocity, velocityCoeffPower);
		}
	}

	@Override
	public void getViewPoint(Vec3 output) {
		Mat4 m = new Mat4();
		Vec4 v = new Vec4();

		m.identity().scale(scale).rotateZ(bodyYaw).translate(head.getTranslation()).rotateZ(headYaw).rotateY(headPitch);

		v.set(head.getViewPoint(), 1);
		m.mul(v);

		output.set(v.x, v.y, v.z);
	}

	public Body getBody() {
		return body;
	}

	public Head getHead() {
		return head;
	}

	public float getBodyYaw() {
		return bodyYaw;
	}

	public float getHeadYaw() {
		return headYaw;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	/**
	 * Returns a number in the range [0; 1] that can be used to scale animation
	 * effects that depend on speed. This parameter is 0 when the entity is not
	 * moving and 1 when it's moving "fast".
	 * 
	 * @return velocity parameter
	 */
	protected float getVelocityParameter() {
		return velocityParameter;
	}

	/**
	 * Returns a number that can be used to parameterize animation effects that
	 * depend on walking. This parameter increases when the entity moves (e.g.
	 * this can be total traveled distance).
	 * 
	 * @return walking parameter
	 */
	protected float getWalkingParameter() {
		return walkingParameter;
	}

	protected float getVelocity() {
		return velocity;
	}

	public float getScale() {
		return scale;
	}

	protected float getWalkingFrequency() {
		return walkingFrequency;
	}

	public NPedModel setWalkingFrequency(float walkingFrequency) {
		this.walkingFrequency = walkingFrequency;
		return this;
	}

	public float getMaxEffectiveVelocity() {
		return maxEffectiveVelocity;
	}

	public float getVelocityCoeffPower() {
		return velocityCoeffPower;
	}

	public NPedModel setMaxEffectiveVelocity(float maxEffectiveVelocity) {
		this.maxEffectiveVelocity = maxEffectiveVelocity;
		return this;
	}

	public NPedModel setVelocityCoeffPower(float velocityCoeffPower) {
		this.velocityCoeffPower = velocityCoeffPower;
		return this;
	}

}
