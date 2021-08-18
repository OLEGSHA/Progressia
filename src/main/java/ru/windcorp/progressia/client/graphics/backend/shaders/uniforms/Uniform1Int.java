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
import java.nio.IntBuffer;

import ru.windcorp.progressia.client.graphics.backend.shaders.Program;

public class Uniform1Int extends Uniform {

	public Uniform1Int(int handle, Program program) {
		super(handle, program);
	}

	public void set(int value) {
		glUniform1i(handle, value);
	}

	public void set(int[] value) {
		glUniform1iv(handle, value);
	}

	public void set(IntBuffer value) {
		glUniform1iv(handle, value);
	}

}
