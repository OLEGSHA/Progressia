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

package ru.windcorp.progressia.test;

import static java.lang.Math.toRadians;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.LambdaModel;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shapes.PppBuilder;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.texture.ComplexTexture;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.entity.HumanoidModel;
import ru.windcorp.progressia.client.world.entity.EntityRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.util.FloatMathUtil;
import ru.windcorp.progressia.common.world.entity.EntityData;

import static java.lang.Math.*;

public class TestEntityRenderHuman extends EntityRender {

	private static final float SECOND_LAYER_OFFSET = 1 / 12f;

	private final Renderable body;
	private final Renderable head;
	private final Renderable leftArm;
	private final Renderable rightArm;
	private final Renderable leftLeg;
	private final Renderable rightLeg;

	private final TexturePrimitive skin;

	public TestEntityRenderHuman(String id) {
		super(id);

		this.skin = fetchSkin();

		ComplexTexture texture = new ComplexTexture(this.skin, 16, 16);

		this.body = createBody(texture);
		this.head = createHead(texture);

		this.leftArm = createLimb(texture, 8, 0, 12, 0, true, true);
		this.rightArm = createLimb(texture, 10, 8, 10, 4, true, false);
		this.leftLeg = createLimb(texture, 4, 0, 0, 0, false, true);
		this.rightLeg = createLimb(texture, 0, 8, 0, 4, false, false);
	}

	protected TexturePrimitive fetchSkin() {
		return EntityRenderRegistry.getEntityTexture("pyotr");
	}

	public TexturePrimitive getSkin() {
		return skin;
	}

	private Renderable createBody(ComplexTexture texture) {
		return createLayeredCuboid(texture, 4, 8, 4, 4, 2, 3, 1, -0.5f, -1, 3, 1, 2, 3);
	}

	private Renderable createHead(ComplexTexture texture) {
		return createLayeredCuboid(texture, 0, 12, 8, 12, 2, 2, 2, -1, -1, 0, 2, 2, 2);
	}

	private Renderable createLimb(ComplexTexture texture, int tx, int ty, int tx2, int ty2, boolean isArm,
			boolean isLeft) {
		Renderable model = createLayeredCuboid(texture, tx, ty, tx2, ty2, 1, 3, 1, -0.5f, -0.5f, isArm ? -2.5f : -3f, 1,
				1, 3);

		if (isArm) {
			return LambdaModel.animate(model, mat -> {
				double phase = GraphicsInterface.getTime() + (isLeft ? 0 : Math.PI / 3);
				mat.rotateX((isLeft ? +1 : -1) * 1 / 40f * (sin(phase) + 1));
				mat.rotateY(1 / 20f * sin(Math.PI / 3 * phase));
			});
		} else {
			return model;
		}
	}

	private Renderable createLayeredCuboid(ComplexTexture texture, int tx, int ty, int tx2, int ty2, int tw, int th,
			int td, float ox, float oy, float oz, float sx, float sy, float sz) {
		WorldRenderProgram program = WorldRenderProgram.getDefault();
		StaticModel.Builder b = StaticModel.builder();

		// First layer
		b.addPart(new PppBuilder(program, texture.getCuboidTextures(tx, ty, tw, th, td)).setOrigin(ox, oy, oz)
				.setSize(sx, sy, sz).create());

		ox -= SECOND_LAYER_OFFSET;
		oy -= SECOND_LAYER_OFFSET;
		oz -= SECOND_LAYER_OFFSET;

		sx += SECOND_LAYER_OFFSET * 2;
		sy += SECOND_LAYER_OFFSET * 2;
		sz += SECOND_LAYER_OFFSET * 2;

		// Second layer
		b.addPart(new PppBuilder(program, texture.getCuboidTextures(tx2, ty2, tw, th, td)).setOrigin(ox, oy, oz)
				.setSize(sx, sy, sz).create());

		return b.build();
	}

	@Override
	public EntityRenderable createRenderable(EntityData entity) {
		return new HumanoidModel(entity,

				new HumanoidModel.Body(body),
				new HumanoidModel.Head(head, new Vec3(0, 0, 6), 70, 25, new Vec3(1.2f, 0, 1.5f)),
				new HumanoidModel.Arm(leftArm, new Vec3(0, +1.5f, 3 + 3 - 0.5f), 0.0f),
				new HumanoidModel.Arm(rightArm, new Vec3(0, -1.5f, 3 + 3 - 0.5f), FloatMathUtil.PI_F),
				new HumanoidModel.Leg(leftLeg, new Vec3(0, +0.5f, 3), FloatMathUtil.PI_F),
				new HumanoidModel.Leg(rightLeg, new Vec3(0, -0.5f, 3), 0.0f),

				1.8f / (3 + 3 + 2)).setWalkingArmSwing((float) toRadians(30)).setWalkingLegSwing((float) toRadians(50))
						.setWalkingFrequency(0.15f / 60.0f);
	}

}
