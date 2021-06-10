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

package ru.windcorp.progressia.common.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.lwjgl.BufferUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import ru.windcorp.progressia.common.util.Named;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Resource extends Named {

	private final ResourceReader resourceReader;

	public Resource(String name, ResourceReader resourceReader) {
		super(name);
		this.resourceReader = resourceReader;
	}

	public InputStream getInputStream() {
		return getResourceReader().read(getName());
	}

	public ResourceReader getResourceReader() {
		return resourceReader;
	}

	public Reader getReader() {
		return new InputStreamReader(getInputStream(), StandardCharsets.UTF_8);
	}

	public String readAsString() {
		try (Reader reader = getReader()) {
			return CharStreams.toString(reader);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not read resource %s as text", this);
		}
	}

	public ByteBuffer readAsBytes(ByteBuffer output) {
		byte[] byteArray;
		try (InputStream stream = getInputStream()) {
			byteArray = ByteStreams.toByteArray(stream);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not read resource %s as bytes", this);
		}

		if (output == null || output.remaining() < byteArray.length) {
			output = BufferUtils.createByteBuffer(byteArray.length);
		}

		int position = output.position();
		output.put(byteArray);
		output.limit(output.position());
		output.position(position);

		return output;
	}

	public ByteBuffer readAsBytes() {
		return readAsBytes(null);
	}

}
