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
package ru.windcorp.optica.client.graphics.backend.shaders.uniforms;

import ru.windcorp.optica.client.graphics.backend.shaders.Program;

import static org.lwjgl.opengl.GL20.*;
import java.nio.IntBuffer;

import glm.vec._2.i.Vec2i;

public class Uniform2Int extends Uniform {

	public Uniform2Int(int handle, Program program) {
		super(handle, program);
	}
	
	public void set(int x, int y) {
		glUniform2i(handle, x, y);
	}
	
	public void set(int[] value) {
		glUniform2iv(handle, value);
	}
	
	public void set(IntBuffer value) {
		glUniform2iv(handle, value);
	}
	
	public void set(Vec2i value) {
		glUniform2i(handle, value.x, value.y);
	}

}
