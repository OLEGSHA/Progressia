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

package ru.windcorp.progressia.client.graphics.backend.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.opengl.GL20;
import ru.windcorp.progressia.client.graphics.backend.OpenGLObjectTracker;
import ru.windcorp.progressia.client.graphics.backend.OpenGLObjectTracker.OpenGLDeletable;
import ru.windcorp.progressia.client.graphics.backend.shaders.attributes.Attribute;
import ru.windcorp.progressia.client.graphics.backend.shaders.uniforms.Uniform;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Program implements OpenGLDeletable {

	private int handle;

	public Program(Shader vertexShader, Shader fragmentShader) {
		handle = glCreateProgram();
		OpenGLObjectTracker.register(this, GL20::glDeleteProgram);

		glAttachShader(handle, vertexShader.getHandle());
		glAttachShader(handle, fragmentShader.getHandle());

		glLinkProgram(handle);

		if (glGetProgrami(handle, GL_LINK_STATUS) == GL_FALSE) {
			throw CrashReports.report(null, "Bad program:\n%s", glGetProgramInfoLog(handle));
		}
	}

	public Attribute getAttribute(String name) {
		return new Attribute(glGetAttribLocation(handle, name), this);
	}

	public Uniform getUniform(String name) {
		return new Uniform(glGetUniformLocation(handle, name), this);
	}

	public void use() {
		glUseProgram(handle);
	}

	@Override
	public int getHandle() {
		return handle;
	}

}
