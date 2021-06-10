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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL;

import ru.windcorp.progressia.client.graphics.GUI;

class LWJGLInitializer {

	private LWJGLInitializer() {
	}

	public static void initialize() {
		checkEnvironment();
		initializeGLFW();
		createWindow();
		positionWindow();
		createWindowIcons();
		initializeOpenGL();
		setupWindowCallbacks();

		glfwShowWindow(GraphicsBackend.getWindowHandle());
	}

	private static void checkEnvironment() {
		// TODO Auto-generated method stub
	}

	private static void initializeGLFW() {
		// TODO Do GLFW error handling: check glfwInit, setup error callback
		glfwInit();
		GraphicsBackend.setGLFWInitialized(true);
	}

	private static void createWindow() {
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

		long handle = glfwCreateWindow(900, 900, "ProgressiaTest", NULL, NULL);

		// TODO Check that handle != NULL

		GraphicsBackend.setWindowHandle(handle);

		glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwMakeContextCurrent(handle);
		glfwSwapInterval(0); // TODO: remove after config system is added
	}

	private static void positionWindow() {
		// TODO Auto-generated method stub

	}

	private static void createWindowIcons() {
		// TODO Auto-generated method stub

	}

	private static void initializeOpenGL() {
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		RenderTaskQueue.schedule(OpenGLObjectTracker::deleteEnqueuedObjects);
		GraphicsBackend.setOpenGLInitialized(true);
	}

	private static void setupWindowCallbacks() {
		long handle = GraphicsBackend.getWindowHandle();

		glfwSetFramebufferSizeCallback(handle, GraphicsBackend::onFrameResized);

		glfwSetKeyCallback(handle, InputHandler::handleKeyInput);
		glfwSetMouseButtonCallback(handle, InputHandler::handleMouseButtonInput);

		glfwSetCursorPosCallback(handle, InputHandler::handleMouseMoveInput);

		glfwSetScrollCallback(handle, InputHandler::handleWheelScroll);

		GraphicsInterface.subscribeToInputEvents(GUI.getEventSubscriber());
	}

}
