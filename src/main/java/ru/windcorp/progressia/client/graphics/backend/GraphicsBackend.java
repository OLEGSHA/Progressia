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
package ru.windcorp.progressia.client.graphics.backend;

import static org.lwjgl.opengl.GL11.*;

import glm.vec._2.i.Vec2i;

import static org.lwjgl.glfw.GLFW.*;

public class GraphicsBackend {
	
	private static RenderThread renderThread;
	
	private static long windowHandle;
	
	private static final Vec2i FRAME_SIZE = new Vec2i();
	
	private static double frameLength = 1.0 / 60; // TODO do something about it
	private static long framesRendered = 0;
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
	
	public static int getFrameWidth() {
		return FRAME_SIZE.x;
	}

	public static int getFrameHeight() {
		return FRAME_SIZE.y;
	}
	
	public static Vec2i getFrameSize() {
		return FRAME_SIZE;
	}

	static void onFrameResized(long window, int newWidth, int newHeight) {
		if (window != windowHandle) return;
		
		InputHandler.handleFrameResize(newWidth, newHeight);
		FRAME_SIZE.set(newWidth, newHeight);
		
		glViewport(0, 0, newWidth, newHeight);
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
	
	public static long getFramesRendered() {
		return framesRendered;
	}

	public static void startNextLayer() {
		glClear(GL_DEPTH_BUFFER_BIT);
	}

}
