/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics.model;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.util.Vectors;

public class Shapes {
	
	public static Shape createParallelepiped( // Try saying that 10 times fast
			ShapeRenderProgram program,
			
			Vec3 origin,
			
			Vec3 width,
			Vec3 height,
			Vec3 depth,
			
			Vec3 colorMultiplier,
			
			Texture topTexture,
			Texture bottomTexture,
			Texture northTexture,
			Texture southTexture,
			Texture eastTexture,
			Texture westTexture
	) {
		
		Vec3 faceOrigin = Vectors.grab3();
		Vec3 faceWidth = Vectors.grab3();
		
		Face top = Faces.createRectangle(
				program,
				topTexture, colorMultiplier,
				faceOrigin.set(origin).add(height).add(width),
				faceWidth.set(width).negate(),
				depth,
				false
		);
		
		Face bottom = Faces.createRectangle(
				program,
				bottomTexture, colorMultiplier,
				origin,
				width,
				depth,
				false
		);
		
		Face north = Faces.createRectangle(
				program,
				northTexture, colorMultiplier,
				faceOrigin.set(origin).add(depth),
				width,
				height,
				false
		);
		
		Face south = Faces.createRectangle(
				program,
				southTexture, colorMultiplier,
				faceOrigin.set(origin).add(width),
				faceWidth.set(width).negate(),
				height,
				false
		);
		
		Face east = Faces.createRectangle(
				program,
				eastTexture, colorMultiplier,
				origin,
				depth,
				height,
				false
		);
		
		Face west = Faces.createRectangle(
				program,
				westTexture, colorMultiplier,
				faceOrigin.set(origin).add(width).add(depth),
				faceWidth.set(depth).negate(),
				height,
				false
		);
		
		Shape result = new Shape(
				Usage.STATIC,
				program,
				top, bottom, north, south, east, west
		);
		
		Vectors.release(faceOrigin);
		Vectors.release(faceWidth);
		
		return result;
	}
	
	public static class PppBuilder {
		
		private final ShapeRenderProgram program;
		
		private final Vec3 origin = new Vec3(-0.5f, -0.5f, -0.5f);

		private final Vec3 depth  = new Vec3(1, 0, 0);
		private final Vec3 width  = new Vec3(0, 1, 0);
		private final Vec3 height = new Vec3(0, 0, 1);
		
		private final Vec3 colorMultiplier = new Vec3(1, 1, 1);
		
		private final Texture topTexture;
		private final Texture bottomTexture;
		private final Texture northTexture;
		private final Texture southTexture;
		private final Texture eastTexture;
		private final Texture westTexture;
		
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
		
		public PppBuilder setColorMultiplier(Vec3 colorMultiplier) {
			this.colorMultiplier.set(colorMultiplier);
			return this;
		}
		
		public PppBuilder setColorMultiplier(float r, float g, float b) {
			this.colorMultiplier.set(r, g, b);
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
			return this.setWidth(x).setDepth(y).setHeight(z);
		}
		
		public Shape create() {
			return createParallelepiped(
					program,
					origin,
					width, height, depth,
					colorMultiplier,
					topTexture,
					bottomTexture,
					northTexture,
					southTexture,
					eastTexture,
					westTexture
			);
		}
		
	}

}
