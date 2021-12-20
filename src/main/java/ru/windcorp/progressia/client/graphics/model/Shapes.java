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
 
package ru.windcorp.progressia.client.graphics.model;

import java.util.Map;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.rels.AbsFace;

public class Shapes {

	public static Shape createParallelepiped(
		// Try saying that 10 times fast
		ShapeRenderProgram program,

		Vec3 origin,

		Vec3 width,
		Vec3 height,
		Vec3 depth,

		Vec4 colorMultiplier,

		Texture topTexture,
		Texture bottomTexture,
		Texture northTexture,
		Texture southTexture,
		Texture eastTexture,
		Texture westTexture,

		boolean flip
	) {

		ShapePart top = ShapeParts.createRectangle(
			program,
			topTexture,
			colorMultiplier,
			origin.add_(height).add(width),
			width.negate_(),
			depth,
			flip
		);

		ShapePart bottom = ShapeParts.createRectangle(
			program,
			bottomTexture,
			colorMultiplier,
			origin,
			width,
			depth,
			flip
		);

		ShapePart north = ShapeParts.createRectangle(
			program,
			northTexture,
			colorMultiplier,
			origin.add_(depth),
			width,
			height,
			flip
		);

		ShapePart south = ShapeParts.createRectangle(
			program,
			southTexture,
			colorMultiplier,
			origin.add_(width),
			width.negate_(),
			height,
			flip
		);

		ShapePart east = ShapeParts.createRectangle(
			program,
			eastTexture,
			colorMultiplier,
			origin,
			depth,
			height,
			flip
		);

		ShapePart west = ShapeParts.createRectangle(
			program,
			westTexture,
			colorMultiplier,
			origin.add_(width).add(depth),
			depth.negate_(),
			height,
			flip
		);

		Shape result = new Shape(
			Usage.STATIC,
			program,
			top,
			bottom,
			north,
			south,
			east,
			west
		);

		return result;
	}
	
	public static Shape createParallelogram(
		// Try saying that 10 times fast
		ShapeRenderProgram program,

		Vec3 origin,

		Vec3 width,
		Vec3 height,

		Vec4 colorMultiplier,

		Texture texture
	) {
		Shape result = new Shape(
			Usage.STATIC,
			program,
			ShapeParts.createRectangle(
				program,
				texture,
				colorMultiplier,
				origin,
				width,
				height,
				false
			)
		);

		return result;
	}

	public static class PppBuilder {

		private final ShapeRenderProgram program;

		private final Vec3 origin = new Vec3(-0.5f, -0.5f, -0.5f);

		private final Vec3 depth = new Vec3(1, 0, 0);
		private final Vec3 width = new Vec3(0, 1, 0);
		private final Vec3 height = new Vec3(0, 0, 1);

		private final Vec4 colorMultiplier = new Vec4(1, 1, 1, 1);

		private final Texture topTexture;
		private final Texture bottomTexture;
		private final Texture northTexture;
		private final Texture southTexture;
		private final Texture eastTexture;
		private final Texture westTexture;

		private boolean flip = false;

		public PppBuilder(
			ShapeRenderProgram program,
			Texture top,
			Texture bottom,
			Texture north,
			Texture south,
			Texture east,
			Texture west
		) {
			this.program = program;
			this.topTexture = top;
			this.bottomTexture = bottom;
			this.northTexture = north;
			this.southTexture = south;
			this.eastTexture = east;
			this.westTexture = west;
		}

		public PppBuilder(
			ShapeRenderProgram program,
			Map<AbsFace, Texture> textureMap
		) {
			this(
				program,
				textureMap.get(AbsFace.POS_Z),
				textureMap.get(AbsFace.NEG_Z),
				textureMap.get(AbsFace.POS_X),
				textureMap.get(AbsFace.NEG_X),
				textureMap.get(AbsFace.NEG_Y),
				textureMap.get(AbsFace.POS_Y)
			);
		}

		public PppBuilder(ShapeRenderProgram program, Texture texture) {
			this(program, texture, texture, texture, texture, texture, texture);
		}

		public PppBuilder setOrigin(Vec3 origin) {
			this.origin.set(origin);
			return this;
		}

		public PppBuilder setOrigin(float x, float y, float z) {
			this.origin.set(x, y, z);
			return this;
		}

		public PppBuilder setColorMultiplier(Vec4 colorMultiplier) {
			this.colorMultiplier.set(colorMultiplier);
			return this;
		}

		public PppBuilder setColorMultiplier(float r, float g, float b) {
			this.colorMultiplier.set(r, g, b, 1);
			return this;
		}

		public PppBuilder setColorMultiplier(float r, float g, float b, float a) {
			this.colorMultiplier.set(r, g, b, a);
			return this;
		}

		public PppBuilder setDepth(Vec3 vector) {
			this.depth.set(vector);
			return this;
		}

		public PppBuilder setDepth(float x, float y, float z) {
			this.depth.set(x, y, z);
			return this;
		}

		public PppBuilder setDepth(float x) {
			this.depth.set(x, 0, 0);
			return this;
		}

		public PppBuilder setWidth(Vec3 vector) {
			this.width.set(vector);
			return this;
		}

		public PppBuilder setWidth(float x, float y, float z) {
			this.width.set(x, y, z);
			return this;
		}

		public PppBuilder setWidth(float y) {
			this.width.set(0, y, 0);
			return this;
		}

		public PppBuilder setHeight(Vec3 vector) {
			this.height.set(vector);
			return this;
		}

		public PppBuilder setHeight(float x, float y, float z) {
			this.height.set(x, y, z);
			return this;
		}

		public PppBuilder setHeight(float z) {
			this.height.set(0, 0, z);
			return this;
		}

		public PppBuilder setSize(float x, float y, float z) {
			return this.setDepth(x).setWidth(y).setHeight(z);
		}

		public PppBuilder setSize(float size) {
			return this.setSize(size, size, size);
		}
		
		public PppBuilder centerAt(float x, float y, float z) {
			origin.set(x, y, z);
			
			origin.mul(2);
			origin.sub(width);
			origin.sub(height);
			origin.sub(depth);
			origin.div(2);
			
			return this;
		}
		
		public PppBuilder apply(Mat4 transform) {
			VectorUtil.applyMat4(origin, transform);
			VectorUtil.rotateOnly(width, transform);
			VectorUtil.rotateOnly(height, transform);
			VectorUtil.rotateOnly(depth, transform);
			return this;
		}
		
		public PppBuilder scale(float factor) {
			origin.mul(factor);
			width.mul(factor);
			height.mul(factor);
			depth.mul(factor);
			return this;
		}

		public PppBuilder flip() {
			this.flip = true;
			return this;
		}

		public Shape create() {
			return createParallelepiped(
				program,
				origin,
				width,
				height,
				depth,
				colorMultiplier,
				topTexture,
				bottomTexture,
				northTexture,
				southTexture,
				eastTexture,
				westTexture,
				flip
			);
		}

	}

	public static class PgmBuilder {
		
		private final ShapeRenderProgram program;

		private final Vec3 origin = new Vec3(0, 0, 0);

		private final Vec3 width = new Vec3(1, 0, 0);
		private final Vec3 height = new Vec3(0, 1, 0);

		private final Vec4 colorMultiplier = new Vec4(1, 1, 1, 1);

		private final Texture texture;

		public PgmBuilder(ShapeRenderProgram program, Texture texture) {
			this.program = program;
			this.texture = texture;
		}

		public PgmBuilder setOrigin(Vec3 origin) {
			this.origin.set(origin);
			return this;
		}

		public PgmBuilder setOrigin(float x, float y, float z) {
			this.origin.set(x, y, z);
			return this;
		}

		public PgmBuilder setColorMultiplier(Vec4 colorMultiplier) {
			this.colorMultiplier.set(colorMultiplier);
			return this;
		}

		public PgmBuilder setColorMultiplier(float r, float g, float b) {
			this.colorMultiplier.set(r, g, b, 1);
			return this;
		}

		public PgmBuilder setColorMultiplier(float r, float g, float b, float a) {
			this.colorMultiplier.set(r, g, b, a);
			return this;
		}

		public PgmBuilder setWidth(Vec3 vector) {
			this.width.set(vector);
			return this;
		}

		public PgmBuilder setWidth(float x, float y, float z) {
			this.width.set(x, y, z);
			return this;
		}

		public PgmBuilder setWidth(float x) {
			this.width.set(x, 0, 0);
			return this;
		}

		public PgmBuilder setHeight(Vec3 vector) {
			this.height.set(vector);
			return this;
		}

		public PgmBuilder setHeight(float x, float y, float z) {
			this.height.set(x, y, z);
			return this;
		}

		public PgmBuilder setHeight(float y) {
			this.height.set(0, y, 0);
			return this;
		}

		public PgmBuilder setSize(float x, float y) {
			return this.setWidth(x).setHeight(y);
		}

		public PgmBuilder setSize(float size) {
			return this.setSize(size, size);
		}
		
		public PgmBuilder centerAt(float x, float y, float z) {
			origin.set(x, y, z);
			
			origin.mul(2);
			origin.sub(width);
			origin.div(2);
			
			return this;
		}

		public Shape create() {
			return createParallelogram(
				program,
				origin,
				width,
				height,
				colorMultiplier,
				texture
			);
		}
		
	}
	
}
