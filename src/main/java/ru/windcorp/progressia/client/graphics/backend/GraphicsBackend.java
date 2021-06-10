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
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GraphicsBackend {

	private static RenderThread renderThread;

	private static long windowHandle;

	private static final Vec2i FRAME_SIZE = new Vec2i();

	private static double frameLength = 1.0 / 60; // TODO do something about it
	private static long framesRendered = 0;
	private static double frameStart = Double.NaN;

	private static boolean faceCullingEnabled = false;

	private static boolean isFullscreen = false;
	private static boolean vSyncEnabled = false;
	private static boolean isGLFWInitialized = false;
	private static boolean isOpenGLInitialized = false;

	private GraphicsBackend() {
	}

	public static boolean isGLFWInitialized() {
		return isGLFWInitialized;
	}

	static void setGLFWInitialized(boolean isGLFWInitialized) {
		GraphicsBackend.isGLFWInitialized = isGLFWInitialized;
	}

	public static boolean isOpenGLInitialized() {
		return isOpenGLInitialized;
	}

	static void setOpenGLInitialized(boolean isOpenGLInitialized) {
		GraphicsBackend.isOpenGLInitialized = isOpenGLInitialized;
	}

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
		if (window != windowHandle)
			return;

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

	public static void setFaceCulling(boolean useFaceCulling) {
		if (useFaceCulling == faceCullingEnabled)
			return;

		if (useFaceCulling) {
			glEnable(GL_CULL_FACE);
		} else {
			glDisable(GL_CULL_FACE);
		}

		faceCullingEnabled = useFaceCulling;
	}

	public static boolean isFullscreen() {
		return isFullscreen;
	}

	public static boolean isVSyncEnabled() {
		return vSyncEnabled;
	}

	public static void setFullscreen() {
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowMonitor(getWindowHandle(), glfwGetPrimaryMonitor(), 0, 0, vidmode.width(), vidmode.height(), 0);
		isFullscreen = true;
	}

	public static void setWindowed() {
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowMonitor(getWindowHandle(), 0, (vidmode.width() - getFrameWidth()) / 2,
				(vidmode.height() - getFrameHeight()) / 2, getFrameWidth(), getFrameHeight(), 0);
		isFullscreen = false;
	}

	public static void setVSyncEnabled(boolean enable) {
		glfwSwapInterval(enable ? 1 : 0);
		vSyncEnabled = enable;
	}

	public static int getRefreshRate() {
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		return vidmode.refreshRate();
	}
}
