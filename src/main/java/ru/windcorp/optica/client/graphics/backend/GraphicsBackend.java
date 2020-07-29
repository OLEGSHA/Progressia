/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.graphics.backend;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class GraphicsBackend {
	
	private static RenderThread renderThread;
	
	private static long windowHandle;
	
	private static int framebufferWidth;
	private static int framebufferHeight;
	
	private static double frameLength = 1.0 / 60; // TODO do something about it
	private static int framesRendered = 0;
	private static double frameStart = Double.NaN;
	
	private GraphicsBackend() {}
	
	public static void initialize() {
		startRenderThread();
	}

	private static void startRenderThread() {
		renderThread = new RenderThread();
		renderThread.start();
	}
	
	public static Thread getRenderThread() {
		return renderThread;
	}
	
	static void setWindowHandle(long windowHandle) {
		GraphicsBackend.windowHandle = windowHandle;
	}
	
	public static long getWindowHandle() {
		return windowHandle;
	}
	
	public static int getFramebufferWidth() {
		return framebufferWidth;
	}

	public static int getFramebufferHeight() {
		return framebufferHeight;
	}

	static void onFramebufferResized(long window, int newWidth, int newHeight) {
		if (window != windowHandle) return;
		
		framebufferWidth = newWidth;
		framebufferHeight = newHeight;
		
		glViewport(0, 0, framebufferWidth, framebufferHeight);
	}

	static void startFrame() {
		double now = glfwGetTime();
		
		if (Double.isNaN(frameStart)) {
			frameStart = now;
		} else {
			frameLength = now - frameStart;
			frameStart = now;
		}
	}
	
	static void endFrame() {
		framesRendered++;
	}
	
	public static double getFrameStart() {
		return frameStart;
	}
	
	public static double getFrameLength() {
		return frameLength;
	}
	
	public static int getFramesRendered() {
		return framesRendered;
	}

}
