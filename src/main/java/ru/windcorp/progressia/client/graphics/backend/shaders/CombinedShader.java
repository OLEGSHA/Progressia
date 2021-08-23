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

import ru.windcorp.progressia.common.resource.Resource;

public class CombinedShader extends Shader {

	public CombinedShader(String... resources) {
		super(getTypeOf(resources), combine(resources));
	}

	private static ShaderType getTypeOf(String[] resources) {
		ShaderType first = ShaderType.guessByResourceName(resources[0]);

		for (int i = 1; i < resources.length; ++i) {
			if (ShaderType.guessByResourceName(resources[i]) != first) {
				throw new IllegalArgumentException(
						"Deduced shader types of " + resources[0] + " and " + resources[i] + " differ");
			}
		}

		return first;
	}

	private static String combine(String[] resources) {
		StringBuilder accumulator = new StringBuilder("#version 120\n");

		for (String resourceName : resources) {
			Resource resource = getShaderResource(resourceName);

			accumulator.append("\n// START " + resourceName);
			accumulator.append(stripVersionAnnotations(resource));
			accumulator.append('\n');
		}

		return accumulator.toString();
	}

	private static String stripVersionAnnotations(Resource resource) {
		String contents = resource.readAsString();

		int versionIndex;
		for (versionIndex = 0; versionIndex < contents.length(); ++versionIndex) {
			if (!Character.isWhitespace(contents.codePointAt(versionIndex)))
				break;
		}

		if (versionIndex < contents.length()) {
			if (contents.codePointAt(versionIndex) == '#') {
				final String versionAnnotation = "#version ";

				if (contents.regionMatches(versionIndex, versionAnnotation, 0, versionAnnotation.length())) {
					contents = contents.substring(versionIndex + versionAnnotation.length() + "120".length());
				}

			}
		}

		return contents;
	}

}
