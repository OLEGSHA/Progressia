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

public class OSContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("OS Name", System.getProperty("os.name"));
		output.put("OS Version", System.getProperty("os.version"));
		output.put("OS Architecture", System.getProperty("os.arch"));
	}

	@Override
	public String getName() {
		return "OS Context Provider";
	}
}
