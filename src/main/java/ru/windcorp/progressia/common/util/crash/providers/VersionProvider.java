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

import java.util.Map;

import ru.windcorp.progressia.Progressia;
import ru.windcorp.progressia.common.util.crash.ContextProvider;

public class VersionProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		output.put("Version", Progressia.getVersion());
		output.put("Git commit", Progressia.getGitCommit());
		output.put("Git branch", Progressia.getGitBranch());
		output.put("Build ID", Progressia.getBuildId());
	}

	@Override
	public String getName() {
		return "Version Provider";
	}

}
