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

import static java.lang.Math.pow;
import static java.lang.Math.toRadians;

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.FloatMathUtil;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
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

		protected void render(
			ShapeRenderHelper renderer,
			NPedModel model
		) {
			applyTransform(renderer.pushTransform(), model);
			renderable.render(renderer);
			renderer.popTransform();
		}

		protected void applyTransform(Mat4 mat, NPedModel model) {
			mat.translate(getTranslation());
		}

		public Vec3 getTranslation() {
			return translation;
		}
		
		public Mat4 getTransform(Mat4 output, NPedModel model) {
			if (output == null) output = new Mat4().identity();
			applyTransform(output, model);
			return output;
		}
	}

	public static class Body extends BodyPart {
		public Body(Renderable renderable) {
			super(renderable, null);
		}
	}

	public static class Head extends BodyPart {
		private final float maxYaw;
		private final float maxPitch;

		private final Vec3 viewPoint;

		public Head(
			Renderable renderable,
			Vec3 joint,
			double maxYawDegrees,
			double maxPitchDegrees,
			Vec3 viewPoint
		) {
			super(renderable, joint);
			this.maxYaw = (float) toRadians(maxYawDegrees);
			this.maxPitch = (float) toRadians(maxPitchDegrees);
			this.viewPoint = viewPoint;
		}

		@Override
		protected void applyTransform(Mat4 mat, NPedModel model) {
			super.applyTransform(mat, model);
			mat.rotateZ(-model.getHeadYaw()).rotateY(-model.getHeadPitch());
		}

		public Vec3 getViewPoint() {
			return viewPoint;
		}
	}

	public static boolean flag;

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

	private final Vec3 bodyLookingAt = new Vec3().set(0);
	private final Mat4 bodyTransform = new Mat4(); 
	
	private float headYaw;
	private float headPitch;

	public NPedModel(EntityData data, Body body, Head head, float scale) {
		super(data);
		this.body = body;
		this.head = head;
		this.scale = scale;

		computeRotations();
	}

	@Override
	protected void doRender(ShapeRenderHelper renderer) {
		renderer.pushTransform().scale(scale).mul(bodyTransform);
		renderBodyParts(renderer);
		renderer.popTransform();
	}

	protected void renderBodyParts(ShapeRenderHelper renderer) {
		body.render(renderer, this);
		head.render(renderer, this);
	}
	
	@Override
	protected void update() {
		advanceTime();
		computeRotations();
	}

	private void computeRotations() {
		if (!bodyLookingAt.any()) {
			getData().getForwardVector(bodyLookingAt);
			headYaw = 0;
		} else {
			ensureBodyLookingAtIsPerpendicularToUpVector();
			computeDesiredHeadYaw();
			clampHeadYawAndChangeBodyLookingAt();
		}
		
		recomputeBodyTransform();

		setHeadPitch();
	}

	private void ensureBodyLookingAtIsPerpendicularToUpVector() {
		Vec3 up = getData().getUpVector();
		if (up.dot(bodyLookingAt) > 1 - 1e-4) return;
		
		Vec3 tmp = Vectors.grab3();
		
		tmp.set(up).mul(-up.dot(bodyLookingAt)).add(bodyLookingAt);
		
		float tmpLength = tmp.length();
		if (tmpLength > 1e-4) {
			bodyLookingAt.set(tmp).div(tmpLength);
		} else {
			// bodyLookingAt is suddenly parallel to up vector -- PANIC! ENTERING RESCUE MODE!
			getData().getForwardVector(bodyLookingAt);
		}
		
		Vectors.release(tmp);
	}

	private void computeDesiredHeadYaw() {
		Vec3 newDirection = getData().getForwardVector(null);
		Vec3 oldDirection = bodyLookingAt;
		Vec3 up = getData().getUpVector();
		
		headYaw = (float) VectorUtil.getAngle(oldDirection, newDirection, up);
	}

	private void clampHeadYawAndChangeBodyLookingAt() {
		float bodyYawChange = 0;
		
		if (headYaw > +head.maxYaw) {
			bodyYawChange = headYaw - +head.maxYaw;
			headYaw = +head.maxYaw;
		} else if (headYaw < -head.maxYaw) {
			bodyYawChange = headYaw - -head.maxYaw;
			headYaw = -head.maxYaw;
		}
		
		if (bodyYawChange != 0) {
			VectorUtil.rotate(bodyLookingAt, getData().getUpVector(), bodyYawChange);
		}
	}

	private void recomputeBodyTransform() {
		Vec3 u = getData().getUpVector();
		Vec3 f = bodyLookingAt;
		Vec3 s = Vectors.grab3();
		
		s.set(u).cross(f);
		
		bodyTransform.identity().set(
			+f.x, -s.x, +u.x,    0,
			+f.y, -s.y, +u.y,    0,
			+f.z, -s.z, +u.z,    0,
			   0,    0,    0,    1
		);
		
		Vectors.release(s);
	}

	private void setHeadPitch() {
		headPitch = Glm.clamp((float) getData().getPitch(), -head.maxPitch, +head.maxPitch);
	}

	private void advanceTime() {
		Vec3 horizontal = getData().getUpVector()
			.mul_(-getData().getUpVector().dot(getData().getVelocity()))
			.add(getData().getVelocity());

		velocity = horizontal.length();

		computeVelocityParameter();

		// TODO switch to world time
		walkingParameter += velocity * GraphicsInterface.getFrameLength() * 1000;
		
		rotateBodyWithMovement(horizontal);
	}

	private void computeVelocityParameter() {
		if (velocity > maxEffectiveVelocity) {
			velocityParameter = 1;
		} else {
			velocityParameter = (float) pow(velocity / maxEffectiveVelocity, velocityCoeffPower);
		}
	}

	private void rotateBodyWithMovement(Vec3 target) {
		if (velocityParameter == 0 || !target.any() || Glm.equals(target, bodyLookingAt)) {
			return;
		}
		
		Vec3 axis = getData().getUpVector();
		
		float yawDifference = FloatMathUtil.normalizeAngle(
			(float) VectorUtil.getAngle(
				bodyLookingAt,
				target.normalize_(),
				axis
			)
		);
		
		float bodyYawChange =
			velocityParameter *
			yawDifference *
			(float) Math.expm1(GraphicsInterface.getFrameLength() * 10);
		
		VectorUtil.rotate(bodyLookingAt, axis, bodyYawChange);
	}

	@Override
	protected void doGetViewPoint(Vec3 output) {
		Mat4 m = new Mat4();
		Vec4 v = new Vec4();

		m.identity()
			.scale(scale)
			.mul(bodyTransform);
		
		head.getTransform(m, this);

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
	
	public Vec3 getBodyLookingAt() {
		return bodyLookingAt;
	}

	public float getHeadYaw() {
		return headYaw;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	/**
	 * Returns a number in the range [0; 1] that can be used to scale animation
	 * effects that depend on speed.
	 * This parameter is 0 when the entity is not moving and 1 when it's moving
	 * "fast".
	 * 
	 * @return velocity parameter
	 */
	protected float getVelocityParameter() {
		return velocityParameter;
	}

	/**
	 * Returns a number that can be used to parameterize animation effects that
	 * depend on walking.
	 * This parameter increases when the entity moves (e.g. this can be total
	 * traveled distance).
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
