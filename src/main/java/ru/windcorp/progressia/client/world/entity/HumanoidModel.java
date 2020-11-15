package ru.windcorp.progressia.client.world.entity;

import static java.lang.Math.*;
import static ru.windcorp.progressia.common.util.FloatMathUtils.*;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class HumanoidModel extends NPedModel {
	
	protected static abstract class Limb extends BodyPart {
		private final float animationOffset;
		
		public Limb(
				Renderable renderable, Vec3 joint,
				float animationOffset
		) {
			super(renderable, joint);
			this.animationOffset = animationOffset;
		}
		
		@Override
		protected void applyTransform(Mat4 mat, NPedModel model) {
			float phase = model.getWalkingFrequency() * model.getWalkingParameter() + animationOffset;
			float value = sin(phase);
			float amplitude = getSwingAmplitude((HumanoidModel) model) * model.getVelocityParameter();
			mat.rotateY(value * amplitude);
		}

		protected abstract float getSwingAmplitude(HumanoidModel model);
		
	}
	
	public static class Leg extends Limb {
		public Leg(
				Renderable renderable, Vec3 joint,
				float animationOffset
		) {
			super(renderable, joint, animationOffset);
		}
		
		@Override
		protected float getSwingAmplitude(HumanoidModel model) {
			return model.walkingLegSwing;
		}
	}
	
	public static class Arm extends Limb {
		public Arm(
				Renderable renderable, Vec3 joint,
				float animationOffset
		) {
			super(renderable, joint, animationOffset);
		}
		
		@Override
		protected float getSwingAmplitude(HumanoidModel model) {
			return model.walkingArmSwing;
		}
	}
	
	private final Arm leftArm;
	private final Arm rightArm;
	private final Leg leftLeg;
	private final Leg rightLeg;
	
	private float walkingLegSwing;
	private float walkingArmSwing;
	
	public HumanoidModel(
			EntityData entity,
			
			Body body, Head head,
			Arm leftArm, Arm rightArm,
			Leg leftLeg, Leg rightLeg,
			
			float scale
	) {
		super(entity, body, head, scale);
		this.leftArm = leftArm;
		this.rightArm = rightArm;
		this.leftLeg = leftLeg;
		this.rightLeg = rightLeg;
	}
	
	@Override
	protected void renderBodyParts(ShapeRenderHelper renderer) {
		super.renderBodyParts(renderer);
		leftArm.render(renderer, this);
		rightArm.render(renderer, this);
		leftLeg.render(renderer, this);
		rightLeg.render(renderer, this);
	}
	
	public float getWalkingArmSwing() {
		return walkingArmSwing;
	}
	
	public float getWalkingLegSwing() {
		return walkingLegSwing;
	}
	
	public HumanoidModel setWalkingLegSwing(float walkingLegSwing) {
		this.walkingLegSwing = walkingLegSwing;
		return this;
	}
	
	public HumanoidModel setWalkingArmSwing(float walkingArmSwing) {
		this.walkingArmSwing = walkingArmSwing;
		return this;
	}

}
