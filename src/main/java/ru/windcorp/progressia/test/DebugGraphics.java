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
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.common.util.Vectors;

public class DebugGraphics {
	
	private static final float TAIL_THICKNESS = 0.03f;
	private static final float HEAD_SIZE = 0.1f;
	
	private static final Renderable THE_VECTOR = StaticModel.builder().addPart(
		new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null)
			.setSize(1.0f, TAIL_THICKNESS, TAIL_THICKNESS)
			.setOrigin(0, -TAIL_THICKNESS / 2, -TAIL_THICKNESS / 2)
			.create()
	).addPart(
		new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null)
			.setSize(HEAD_SIZE, HEAD_SIZE, HEAD_SIZE)
			.setOrigin((1 - HEAD_SIZE / 2), -HEAD_SIZE / 2, -HEAD_SIZE / 2)
			.create()
	).build();
	
	public static void drawVector(Vec3 vector, Vec4 color, Vec3 origin, float scale, ShapeRenderHelper renderer) {
		float length = vector.length();
		if (length == 0) return;
		
		if (scale == 0) scale = 1 / length;
		
		Mat4 mat = renderer.pushTransform();
		
		mat.translate(origin);
		
		Vec3 somePerpendicular = new Vec3();
		
		if (Math.abs(vector.z) > (1 - 1e-4f) * length) {
			somePerpendicular.set(1, 0, 0);
		} else {
			somePerpendicular.set(0, 0, 1);
		}
		
		Vec3 f = vector;
		Vec3 s = somePerpendicular.cross_(f).normalize();
		Vec3 u = somePerpendicular.set(f).cross(s).normalize();
		
		// @formatter:off
		mat.mul(new Mat4(
			+f.x * scale, +f.y * scale, +f.z * scale,    0,
			        -s.x,         -s.y,         -s.z,    0,
			        +u.x,         +u.y,         +u.z,    0,
			           0,            0,            0,    1
		));
		// @formatter:on
		
		renderer.pushColorMultiplier().mul(color);
		THE_VECTOR.render(renderer);
		renderer.popColorMultiplier();

		renderer.popTransform();
	}
	
	public static void drawVector(Vec3 vector, ShapeRenderHelper renderer) {
		drawVector(vector, Colors.GRAY_A, Vectors.ZERO_3, 1, renderer);
	}
	
	public static void drawDirection(Vec3 vector, ShapeRenderHelper renderer) {
		drawVector(vector, Colors.GRAY_A, Vectors.ZERO_3, 0, renderer);
	}

}
