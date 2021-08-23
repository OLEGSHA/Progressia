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

package ru.windcorp.progressia.client.graphics.backend.shaders.uniforms;

import static org.lwjgl.opengl.GL20.*;
import java.nio.FloatBuffer;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.shaders.Program;

public class Uniform3Float extends Uniform {

	public Uniform3Float(int handle, Program program) {
		super(handle, program);
	}

	public void set(float x, float y, float z) {
		glUniform3f(handle, x, y, z);
	}

	public void set(float[] value) {
		glUniform3fv(handle, value);
	}

	public void set(FloatBuffer value) {
		glUniform3fv(handle, value);
	}

	public void set(Vec3 value) {
		glUniform3f(handle, value.x, value.y, value.z);
	}

}
