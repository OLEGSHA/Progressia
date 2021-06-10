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

import static java.lang.Math.*;
import static ru.windcorp.progressia.common.util.FloatMathUtil.*;
import static ru.windcorp.progressia.common.util.FloatMathUtil.sin;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class QuadripedModel extends NPedModel {

	public static class Leg extends BodyPart {
		private final float animationOffset;

		public Leg(Renderable renderable, Vec3 joint, float animationOffset) {
			super(renderable, joint);
			this.animationOffset = animationOffset;
		}

		@Override
		protected void applyTransform(Mat4 mat, NPedModel model) {
			float phase = model.getWalkingFrequency() * model.getWalkingParameter() + animationOffset;
			float value = sin(phase);
			float amplitude = ((QuadripedModel) model).getWalkingSwing() * model.getVelocityParameter();
			mat.rotateY(value * amplitude);
		}

	}

	private final Leg leftForeLeg, rightForeLeg;
	private final Leg leftHindLeg, rightHindLeg;

	private float walkingSwing = (float) toRadians(30);

	public QuadripedModel(EntityData entity,

			Body body, Head head, Leg leftForeLeg, Leg rightForeLeg, Leg leftHindLeg, Leg rightHindLeg,

			float scale) {
		super(entity, body, head, scale);

		this.leftForeLeg = leftForeLeg;
		this.rightForeLeg = rightForeLeg;
		this.leftHindLeg = leftHindLeg;
		this.rightHindLeg = rightHindLeg;
	}

	@Override
	protected void renderBodyParts(ShapeRenderHelper renderer) {
		super.renderBodyParts(renderer);
		this.leftForeLeg.render(renderer, this);
		this.rightForeLeg.render(renderer, this);
		this.leftHindLeg.render(renderer, this);
		this.rightHindLeg.render(renderer, this);
	}

	public float getWalkingSwing() {
		return walkingSwing;
	}

	public QuadripedModel setWalkingSwing(float walkingSwing) {
		this.walkingSwing = walkingSwing;
		return this;
	}

}
