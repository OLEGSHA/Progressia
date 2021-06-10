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

import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.backend.shaders.Program;

public class Uniform4Matrix extends Uniform {

	public Uniform4Matrix(int handle, Program program) {
		super(handle, program);
	}

	public void set(float[] value) {
		glUniformMatrix4fv(handle, false, value);
	}

	public void set(FloatBuffer value) {
		glUniformMatrix4fv(handle, false, value);
	}

	private static final float[] BUFFER = new float[4 * 4];

	public void set(Mat4 value) {
		glUniformMatrix4fv(handle, false, value.toFa(BUFFER));
	}

}
