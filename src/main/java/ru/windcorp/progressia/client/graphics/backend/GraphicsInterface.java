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

package ru.windcorp.progressia.client.graphics.backend;

import glm.vec._2.i.Vec2i;

public class GraphicsInterface {

	private GraphicsInterface() {
	}

	public static Thread getRenderThread() {
		return GraphicsBackend.getRenderThread();
	}

	public static boolean isRenderThread() {
		return Thread.currentThread() == getRenderThread();
	}

	public static int getFrameWidth() {
		return GraphicsBackend.getFrameWidth();
	}

	public static int getFrameHeight() {
		return GraphicsBackend.getFrameHeight();
	}

	public static Vec2i getFrameSize() {
		return GraphicsBackend.getFrameSize();
	}

	public static float getAspectRatio() {
		return ((float) getFrameWidth()) / getFrameHeight();
	}

	public static double getTime() {
		return GraphicsBackend.getFrameStart();
	}

	public static double getFrameLength() {
		return GraphicsBackend.getFrameLength();
	}

	public static double getFPS() {
		return 1 / GraphicsBackend.getFrameLength();
	}

	public static long getFramesRendered() {
		return GraphicsBackend.getFramesRendered();
	}

	public static void subscribeToInputEvents(Object listener) {
		InputHandler.register(listener);
	}

	public static void startNextLayer() {
		GraphicsBackend.startNextLayer();
	}

	public static void makeFullscreen(boolean state) {
		if (state) {
			GraphicsBackend.setFullscreen();
		} else {
			GraphicsBackend.setWindowed();
		}
		GraphicsBackend.setVSyncEnabled(GraphicsBackend.isVSyncEnabled());
	}

}
