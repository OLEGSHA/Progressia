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

import java.util.ArrayList;
import java.util.List;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.LambdaModel;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.Shapes.PppBuilder;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.texture.ComplexTexture;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.entity.EntityRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.client.world.entity.QuadripedModel;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class TestEntityRenderJavapony extends EntityRender {

	private final Renderable body;
	private final Renderable head;
	private final Renderable leftForeLeg;
	private final Renderable leftHindLeg;
	private final Renderable rightForeLeg;
	private final Renderable rightHindLeg;

	public TestEntityRenderJavapony(String id) {
		super(id);

		ComplexTexture texture = new ComplexTexture(EntityRenderRegistry.getEntityTexture("javapony"), 256, 128);

		this.body = createBody(texture);
		this.head = createHead(texture);
		this.leftForeLeg = createLeg(texture, 160, 0, true);
		this.rightForeLeg = createLeg(texture, 160, 0, false);
		this.leftHindLeg = createLeg(texture, 0, 0, true);
		this.rightHindLeg = createLeg(texture, 0, 0, false);
	}

	private static Renderable createBody(ComplexTexture texture) {
		LambdaModel.Builder b = LambdaModel.lambdaBuilder();

		b.addStaticPart(createMainBody(texture));

		Texture tailStartTexture = texture.get(128, 96, 8, 32);

		b.addStaticPart(new PppBuilder(WorldRenderProgram.getDefault(),
				BlockFace.mapToFaces(tailStartTexture, tailStartTexture, tailStartTexture, tailStartTexture,
						tailStartTexture, tailStartTexture)).setOrigin(-60, -4, 14).setDepth(32, 0, -16).setWidth(8)
								.setHeight(8).create());

		Texture neckTexture = texture.get(0, 48, 16, 16);

		b.addStaticPart(new PppBuilder(WorldRenderProgram.getDefault(),
				BlockFace.mapToFaces(neckTexture, neckTexture, neckTexture, neckTexture, neckTexture, neckTexture))
						.setOrigin(0, -8, 8).setWidth(16).setDepth(16).setHeight(2, 0, 16).create());

		b.addDynamicPart(createTail(texture),
				m -> m.translate(-60, 0, 24).rotateX(0.05f * Math.sin(GraphicsInterface.getTime()))
						.rotateY(0.05f * Math.sin(Math.PI / 3 * GraphicsInterface.getTime())));

		return new LambdaModel(b);
	}

	private static Renderable createMainBody(ComplexTexture texture) {
		WorldRenderProgram program = WorldRenderProgram.getDefault();
		List<Face> faces = new ArrayList<>();

		// F BODY
		faces.add(Faces.createRectangle(program, texture.get(80, 16, 32, 32), Colors.WHITE, new Vec3(+16, -16, -16),
				new Vec3(0, +32, 0), new Vec3(0, 0, +32), false));

		// NECK BASE
		faces.add(Faces.createRectangle(program, texture.get(80, 48, 32, 16), Colors.WHITE, new Vec3(+16, -16, +16),
				new Vec3(0, +32, 0), new Vec3(-16, 0, 0), false));

		// T BODY (BACK)
		faces.add(Faces.createRectangle(program, texture.get(128, 0, 32, 48), Colors.WHITE, new Vec3(0, -16, +16),
				new Vec3(0, +32, 0), new Vec3(-48, 0, 0), false));

		// BOTTOM B (upper)
		faces.add(Faces.createRectangle(program, texture.get(144, 48, 32, 16), Colors.WHITE, new Vec3(-48, -16, 0),
				new Vec3(0, 32, 0), new Vec3(0, 0, 16), true));

		// BOTTOM B (lower)
		faces.add(Faces.createRectangle(program, texture.get(144, 48, 32, 16), Colors.WHITE, new Vec3(-48, -16, -16),
				new Vec3(0, 32, 0), new Vec3(0, 0, 16), true));

		// BOTTOM B (stomach)
		faces.add(Faces.createRectangle(program, texture.get(144, 48, 32, 16), Colors.WHITE, new Vec3(-48, -16, -16),
				new Vec3(0, 32, 0), new Vec3(16, 0, 0), false));

		// STOMACH
		faces.add(Faces.createRectangle(program, texture.get(224, 96, 32, 32), Colors.WHITE, new Vec3(-32, -16, -16),
				new Vec3(0, 32, 0), new Vec3(32, 0, 0), false));

		// BOTTOM F
		faces.add(Faces.createRectangle(program, texture.get(112, 48, 32, 16), Colors.WHITE, new Vec3(+16, -16, -16),
				new Vec3(0, 32, 0), new Vec3(-16, 0, 0), true));

		// BODY L
		faces.add(Faces.createRectangle(program, texture.get(112, 16, 16, 32), Colors.WHITE, new Vec3(+16, +16, -16),
				new Vec3(-16, 0, 0), new Vec3(0, 0, +32), false));

		// BODY SIDES (left)
		faces.add(Faces.createRectangle(program, texture.get(96, 96, 32, 32), Colors.WHITE, new Vec3(0, +16, -16),
				new Vec3(-32, 0, 0), new Vec3(0, 0, +32), false));

		// QT MARK (left)
		faces.add(Faces.createRectangle(program, texture.get(16, 96, 16, 32), Colors.WHITE, new Vec3(-32, +16, -16),
				new Vec3(-16, 0, 0), new Vec3(0, 0, +32), false));

		// BODY R
		faces.add(Faces.createRectangle(program, texture.get(64, 16, 16, 32), Colors.WHITE, new Vec3(0, -16, -16),
				new Vec3(+16, 0, 0), new Vec3(0, 0, +32), false));

		// BODY SIDES (right)
		faces.add(Faces.createRectangle(program, texture.get(96, 96, 32, 32), Colors.WHITE, new Vec3(0, -16, -16),
				new Vec3(-32, 0, 0), new Vec3(0, 0, +32), true));

		// QT MARK (right)
		faces.add(Faces.createRectangle(program, texture.get(16, 96, 16, 32), Colors.WHITE, new Vec3(-32, -16, -16),
				new Vec3(-16, 0, 0), new Vec3(0, 0, +32), true));

		return new Shape(Usage.STATIC, program, faces.toArray(new Face[faces.size()]));
	}

	private static Renderable createHead(ComplexTexture texture) {
		WorldRenderProgram program = WorldRenderProgram.getDefault();
		StaticModel.Builder b = StaticModel.builder();

		// Head
		b.addPart(new PppBuilder(program, texture.getCuboidTextures(0, 64, 32)).setOrigin(-16, -16, 0).setSize(32)
				.create());

		final float hairOffset = 1f;

		// Hair
		b.addPart(new PppBuilder(program, texture.getCuboidTextures(128, 64, 32))
				.setOrigin(-16 - hairOffset, -16 - hairOffset, -hairOffset).setSize(32 + 2 * hairOffset).create());

		// Right ear
		b.addPart(new PppBuilder(program, texture.getCuboidTextures(48, 128 - 80, 8)).setOrigin(-16 + 3, -16, 32)
				.setSize(8).create());

		// Left ear
		b.addPart(new PppBuilder(program, texture.getCuboidTextures(48, 128 - 80, 8)).setOrigin(-16 + 3, +16, 32)
				.setSize(8, -8, 8).flip().create());

		// Muzzle
		b.addPart(new PppBuilder(program,
				BlockFace.mapToFaces(texture.get(32, 64, 0, 0), texture.get(32, 64, 0, 0),
						texture.get(32 + 8, 64, 16, 8), texture.get(32, 64, 0, 0), texture.get(32, 64, 0, 0),
						texture.get(32, 64, 0, 0))).setOrigin(16, -8, 0).setSize(4, 16, 8).create());

		// Nose
		b.addPart(new PppBuilder(program,
				BlockFace.mapToFaces(texture.get(32, 64, 0, 0), texture.get(32, 64, 0, 0),
						texture.get(32 + 12, 64 + 8, 8, 4), texture.get(32, 64, 0, 0), texture.get(32, 64, 0, 0),
						texture.get(32, 64, 0, 0))).setOrigin(16, -4, 8).setSize(4, 8, 4).create());

		return b.build();
	}

	private static Renderable createLeg(ComplexTexture texture, int textureX, int textureY, boolean isLeft) {
		PppBuilder b = new PppBuilder(WorldRenderProgram.getDefault(),
				texture.getCuboidTextures(textureX, textureY, 16, 48, 16)).setOrigin(-8, isLeft ? +8 : -8, -48)
						.setSize(16, isLeft ? -16 : +16, 48);

		if (isLeft)
			b.flip();

		return b.create();
	}

	private static Renderable createTail(ComplexTexture texture) {
		WorldRenderProgram program = WorldRenderProgram.getDefault();
		StaticModel.Builder b = StaticModel.builder();

		// Main tail
		b.addPart(new PppBuilder(program,
				BlockFace.mapToFaces(texture.get(128, 96, 16, 16), texture.get(128, 96, 16, 16),
						texture.get(128, 96, 16, 32), texture.get(128, 96, 16, 32), texture.get(144, 96, 16, 32),
						texture.get(144, 96, 16, 32))).setOrigin(-8, -8, -32).setSize(16, 16, 32).create());

		return b.build();
	}

	@Override
	public EntityRenderable createRenderable(EntityData entity) {
		return new QuadripedModel(entity,

				new QuadripedModel.Body(body),
				new QuadripedModel.Head(head, new Vec3(12, 0, 20), 120, 45, new Vec3(16, 0, 20)),
				new QuadripedModel.Leg(leftForeLeg, new Vec3(6, +8.1f, -16), 0.0f),
				new QuadripedModel.Leg(rightForeLeg, new Vec3(6, -8.1f, -16), 2.5f),
				new QuadripedModel.Leg(leftHindLeg, new Vec3(-36, +8.2f, -16), 2.5f),
				new QuadripedModel.Leg(rightHindLeg, new Vec3(-36, -8.2f, -16), 0.0f),

				1 / 96f);
	}

}
