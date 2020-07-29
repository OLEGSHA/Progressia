/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.graphics.texture;

import java.util.Objects;

import glm.vec._2.Vec2;

public class Sprite {
	
	private static final Vec2 ORIGIN = new Vec2(0, 0);
	private static final Vec2 FULL_PRIMITIVE = new Vec2(1, 1);
	
	private final TexturePrimitive primitive;
	
	private final Vec2 start;
	private final Vec2 size;
	
	public Sprite(TexturePrimitive primitive, Vec2 start, Vec2 size) {
		this.primitive = Objects.requireNonNull(primitive, "primitive");
		this.start = Objects.requireNonNull(start, "start");
		this.size = Objects.requireNonNull(size, "size");
	}
	
	public Sprite(TexturePrimitive primitive) {
		this(primitive, ORIGIN, FULL_PRIMITIVE);
	}
	
	public TexturePrimitive getPrimitive() {
		return primitive;
	}
	
	public Vec2 getStart() {
		return start;
	}
	
	public Vec2 getSize() {
		return size;
	}

}
