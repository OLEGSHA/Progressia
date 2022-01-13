/*
 * Progressia
 * Copyright (C)  2020-2022  Wind Corporation and contributors
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
package ru.windcorp.progressia.client.graphics.backend;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import ru.windcorp.jputil.ConstantsMapException;
import ru.windcorp.jputil.IntConstantsMap;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class GLFWErrorHandler {

	private static final IntConstantsMap ERROR_CODES;

	static {
		try {
			ERROR_CODES = IntConstantsMap.from(GLFW.class)
				.stripPrefix("GLFW_")
				.onlyValued(i -> i >= 0x10000 && i <= 0x1FFFF)
				.extra("GLFW_NO_ERROR")
				.scan();
		} catch (ConstantsMapException e) {
			throw CrashReports.report(e, "Could not analyze GLFW error codes");
		}
	}

	public void onError(int errorCode, long descriptionPointer) {
		String description = GLFWErrorCallback.getDescription(descriptionPointer);

		String errorCodeName;
		if (ERROR_CODES.hasConstant(errorCode)) {
			errorCodeName = ERROR_CODES.getName(errorCode);
		} else {
			errorCodeName = "<unknown " + Integer.toHexString(errorCode) + ">";
		}

		throw CrashReports.report(null, "GLFW error detected: " + errorCodeName + " %s", description);
	}

}
