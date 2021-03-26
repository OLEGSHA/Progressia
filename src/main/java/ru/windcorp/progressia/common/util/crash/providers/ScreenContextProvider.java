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

import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class ScreenContextProvider implements ContextProvider {

	@Override
	public void provideContext(Map<String, String> output) {
		if (GraphicsBackend.isGLFWInitialized()) {
			output.put("Refresh rate", GraphicsBackend.getRefreshRate() + " Hz");
			output.put("Size", GraphicsBackend.getFrameWidth() + "x" + GraphicsBackend.getFrameHeight());
			output.put("Fullscreen", GraphicsBackend.isFullscreen() ? "enabled" : "disabled");
		}
	}

	@Override
	public String getName() {
		return "Screen Context Provider";
	}
}
