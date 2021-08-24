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

import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes.PppBuilder;
import ru.windcorp.progressia.client.graphics.texture.ComplexTexture;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.entity.EntityRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class TestEntityRenderStatie extends EntityRender {

	private final static int PARTICLE_COUNT = 16;
	private final Renderable core;
	private final Renderable particle;

	public TestEntityRenderStatie(String id) {
		super(id);

		TexturePrimitive texturePrimitive = EntityRenderRegistry.getEntityTexture("Statie");
		ComplexTexture texture = new ComplexTexture(texturePrimitive, 4, 4);
		WorldRenderProgram program = WorldRenderProgram.getDefault();

		final float coreSize = 1f / 4;
		final float particleSize = 1f / 16;

		core = new PppBuilder(program, texture.getCuboidTextures(0, 2, 1)).setSize(coreSize).centerAt(0, 0, 0).create();
		particle = new PppBuilder(program, texture.getCuboidTextures(0, 0, 1)).setSize(particleSize)
			.centerAt(2.5f * coreSize, 0, 0).create();

	}

	@Override
	public EntityRenderable createRenderable(EntityData entity) {
		return new EntityRenderable(entity) {
			@Override
			public void doRender(ShapeRenderHelper renderer) {
				double phase = GraphicsInterface.getTime();
				renderer.pushTransform().translate(0, 0, (float) Math.sin(phase) * 0.1f);

				renderer.pushTransform().scale(
					((TestEntityDataStatie) entity).getSize() / 24.0f
				).rotateY((float) -Math.sin(phase - Math.PI / 3) * Math.PI / 12);

				core.render(renderer);

				renderer.popTransform();
				renderer.popTransform();
				renderer.pushTransform().translate(0, 0, (float) Math.sin(phase + Math.PI / 2) * 0.05f);

				for (int i = 0; i < PARTICLE_COUNT; ++i) {
					double phaseOffset = 2 * Math.PI / PARTICLE_COUNT * i;
					renderer.pushTransform()
						.translate((float) Math.sin(phase + phaseOffset) * 0.1f, 0, 0)
						.rotateX(Math.sin(phase / 2 + phaseOffset) * Math.PI / 6)
						.rotateZ(phase + phaseOffset * 2);
					particle.render(renderer);
					renderer.popTransform();
				}

			}
		};
	}

}
