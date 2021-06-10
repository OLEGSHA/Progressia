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

package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class JavaVersionContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		// JAVA
		output.put("Java version", System.getProperty("java.version"));
		output.put("Java vendor", System.getProperty("java.vendor"));
		output.put("Java home path", System.getProperty("java.home"));
		// VM
		output.put("JVM vendor", System.getProperty("java.vm.vendor"));
		output.put("JVM name", System.getProperty("java.vm.name"));
		output.put("JVM version", System.getProperty("java.vm.version"));
		// Runtime
		output.put("Java Runtime name", System.getProperty("java.runtime.name"));
		output.put("Java Runtime version", System.getProperty("java.runtime.version"));

	}

	@Override
	public String getName() {
		return "Java Version Context Provider";
	}
}
