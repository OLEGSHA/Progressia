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
package ru.windcorp.progressia.common.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.common.io.CharStreams;

import ru.windcorp.progressia.Progressia;

public class Resource {
	
	private final String name;
	
	public Resource(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public InputStream getInputStream() {
		// TODO Do proper resource lookup
		return Progressia.class.getClassLoader().getResourceAsStream(name);
	}
	
	public Reader getReader() {
		return new InputStreamReader(getInputStream());
	}
	
	public String readAsString() {
		try (Reader reader = getReader()) {
			return CharStreams.toString(reader);
		} catch (IOException e) {
			throw new RuntimeException(e); // TODO handle gracefully
		}
	}

}
