package ru.windcorp.progressia.client.world.entity;

import static java.lang.Math.*;
import static ru.windcorp.progressia.common.util.FloatMathUtils.*;

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class QuadripedModel extends EntityRenderable {
	
	private static abstract class BodyPart {
		private final Renderable renderable;
		private final Vec3 translation = new Vec3();

		public BodyPart(Renderable renderable, Vec3 joint) {
			this.renderable = renderable;
			if (joint != null) {
				this.translation.set(joint);
			}
		}
		
		
		protected void render(
				ShapeRenderHelper renderer, QuadripedModel model
		) {
			renderer.pushTransform().translate(translation);
			applyTransform(renderer.pushTransform(), model);
			renderable.render(renderer);
			renderer.popTransform();
			renderer.popTransform();
		}

		protected abstract void applyTransform(Mat4 mat, QuadripedModel model);
		
		public Vec3 getTranslation() {
			return translation;
		}
	}
	
	public static class Body extends BodyPart {
		public Body(Renderable renderable) {
			super(renderable, null);
		}
		
		@Override
		protected void applyTransform(Mat4 mat, QuadripedModel model) {
			// Do nothing
		}
	}
	
	public static class Head extends BodyPart {
		private final float maxYaw;
		private final float maxPitch;
		
		private final Vec3 viewPoint;
		
		public Head(
				Renderable renderable, Vec3 joint,
				double maxYawDegrees, double maxPitchDegrees,
				Vec3 viewPoint
		) {
			super(renderable, joint);
			this.maxYaw = (float) toRadians(maxYawDegrees);
			this.maxPitch = (float) toRadians(maxPitchDegrees);
			this.viewPoint = viewPoint;
		}
		
		@Override
		protected void applyTransform(Mat4 mat, QuadripedModel model) {
			mat.rotateZ(model.headYaw).rotateY(model.headPitch);
		}
		
		public Vec3 getViewPoint() {
			return viewPoint;
		}
	}
	
	public static class Leg extends BodyPart {
		private final float animationOffset;
		
		public Leg(
				Renderable renderable, Vec3 joint,
				float animationOffset
		) {
			super(renderable, joint);
			this.animationOffset = animationOffset;
		}
		
		@Override
		protected void applyTransform(Mat4 mat, QuadripedModel model) {
			mat.rotateY(sin(model.walkingFrequency * model.walkingAnimationParameter + animationOffset) * model.walkingSwing * model.velocityCoeff);
		}
	}
	
	private final Body body;
	private final Head head;
	private final Leg leftForeLeg, rightForeLeg;
	private final Leg leftHindLeg, rightHindLeg;
	
	private final float scale;
	
	private float walkingAnimationParameter = 0;
	private float velocityCoeff = 0;
	private float velocity = 0;
	
	/**
	 * Controls how quickly velocityCoeff approaches 1
	 */
	private float velocityCutoff = 10;
	
	private float walkingFrequency = 0.15f / 60.0f;
	private float walkingSwing = (float) toRadians(30);
	
	private float bodyYaw = Float.NaN;
	private float headYaw;
	private float headPitch;

	public QuadripedModel(
			EntityData entity,
			
			Body body, Head head,
			Leg leftForeLeg, Leg rightForeLeg,
			Leg leftHindLeg, Leg rightHindLeg,
			
			float scale
	) {
		super(entity);
		
		this.body = body;
		this.head = head;
		this.leftForeLeg = leftForeLeg;
		this.rightForeLeg = rightForeLeg;
		this.leftHindLeg = leftHindLeg;
		this.rightHindLeg = rightHindLeg;
		
		this.scale = scale;
	}
	
	@Override
	public void render(ShapeRenderHelper renderer) {
		renderer.pushTransform().scale(scale).rotateZ(bodyYaw);
		body.render(renderer, this);
		head.render(renderer, this);
		leftForeLeg.render(renderer, this);
		rightForeLeg.render(renderer, this);
		leftHindLeg.render(renderer, this);
		rightHindLeg.render(renderer, this);
		renderer.popTransform();
		
		accountForVelocity();
		evaluateAngles();
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
		
		headPitch = Glm.clamp(
				getData().getPitch(),
				-head.maxPitch, head.maxPitch
		);
	}

	private void accountForVelocity() {
		Vec3 horizontal = Vectors.grab3();
		horizontal.set(getData().getVelocity());
		horizontal.z = 0;
		
		velocity = horizontal.length();
		
		evaluateVelocityCoeff();
		
		// TODO switch to world time
		walkingAnimationParameter += velocity * GraphicsInterface.getFrameLength() * 1000;
		
		bodyYaw += velocityCoeff * normalizeAngle(
				(float) (atan2(horizontal.y, horizontal.x) - bodyYaw)
		) * min(1, GraphicsInterface.getFrameLength() * 10);
		Vectors.release(horizontal);
	}
	
	private void evaluateVelocityCoeff() {
		if (velocity * velocityCutoff > 1) {
			velocityCoeff = 1;
		} else {
			velocityCoeff = velocity * velocityCutoff;
			velocityCoeff *= velocityCoeff;
		}
	}
	
	@Override
	public void getViewPoint(Vec3 output) {
		Mat4 m = Matrices.grab4();
		Vec4 v = Vectors.grab4();
		
		m.identity()
		.scale(scale)
		.rotateZ(bodyYaw)
		.translate(head.getTranslation())
		.rotateZ(headYaw)
		.rotateY(headPitch);
		
		v.set(head.getViewPoint(), 1);
		m.mul(v);
		
		output.set(v.x, v.y, v.z);
		
		Vectors.release(v);
		Matrices.release(m);
	}

}
