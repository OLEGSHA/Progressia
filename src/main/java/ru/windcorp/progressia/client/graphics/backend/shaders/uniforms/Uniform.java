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

import ru.windcorp.progressia.client.graphics.backend.shaders.Program;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Uniform {

	protected final int handle;
	private final Program program;

	public Uniform(int handle, Program program) {
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

	public Uniform1Float as1Float() {
		return new Uniform1Float(handle, program);
	}

	public Uniform1Int as1Int() {
		return new Uniform1Int(handle, program);
	}

	public Uniform2Float as2Float() {
		return new Uniform2Float(handle, program);
	}

	public Uniform2Int as2Int() {
		return new Uniform2Int(handle, program);
	}

	public Uniform3Float as3Float() {
		return new Uniform3Float(handle, program);
	}

	public Uniform3Int as3Int() {
		return new Uniform3Int(handle, program);
	}

	public Uniform4Float as4Float() {
		return new Uniform4Float(handle, program);
	}

	public Uniform4Int as4Int() {
		return new Uniform4Int(handle, program);
	}

	public Uniform2Matrix as2Matrix() {
		return new Uniform2Matrix(handle, program);
	}

	public Uniform3Matrix as3Matrix() {
		return new Uniform3Matrix(handle, program);
	}

	public Uniform4Matrix as4Matrix() {
		return new Uniform4Matrix(handle, program);
	}

}
