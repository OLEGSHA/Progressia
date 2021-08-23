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

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.common.collision.AABBoid;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.collision.CompoundCollisionModel;

public class CollisionModelRenderer {

	private static final Shape CUBE = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null)
			.setColorMultiplier(1.0f, 0.7f, 0.2f).create();
	private static final Shape CUBE_GRAY = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null)
			.setColorMultiplier(0.5f, 0.5f, 0.5f).create();

	public static void renderCollisionModel(CollisionModel model, ShapeRenderHelper helper) {
		if (model instanceof AABBoid) {
			renderAABBoid((AABBoid) model, helper);
		} else if (model instanceof CompoundCollisionModel) {
			renderCompound((CompoundCollisionModel) model, helper);
		} else {
			// Ignore silently
		}
	}

	private static void renderAABBoid(AABBoid aabb, ShapeRenderHelper helper) {
		Mat4 mat = helper.pushTransform();
		Vec3 tmp = new Vec3();

		aabb.getOrigin(tmp);
		mat.translate(tmp);
		aabb.getSize(tmp);
		mat.scale(tmp);

		CUBE.render(helper);
		helper.popTransform();
	}

	private static void renderCompound(CompoundCollisionModel model, ShapeRenderHelper helper) {
		for (CollisionModel part : model.getModels()) {
			renderCollisionModel(part, helper);
		}
	}

	public static void renderBlock(Vec3i coords, ShapeRenderHelper helper) {
		helper.pushTransform().translate(coords.x, coords.y, coords.z).scale(0.25f);
		CUBE_GRAY.render(helper);
		helper.popTransform();
	}

}
