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

package ru.windcorp.progressia.client.graphics.backend.shaders.attributes;

import ru.windcorp.progressia.client.graphics.backend.shaders.Program;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Attribute {

	protected final int handle;
	private final Program program;

	public Attribute(int handle, Program program) {
		if (handle < 0) {
			throw CrashReports.report(null, "Bad handle: %d", handle);
		}

		this.handle = handle;
		this.program = program;
	}

	public int getHandle() {
		return handle;
	}

	public Program getProgram() {
		return program;
	}

	public AttributeVertexArray asVertexArray() {
		return new AttributeVertexArray(handle, program);
	}

}
