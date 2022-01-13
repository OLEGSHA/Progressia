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

import java.io.IOException;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.Progressia;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.input.FrameResizeEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.texture.TextureDataEditor;
import ru.windcorp.progressia.client.graphics.texture.TextureLoader;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

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
		GraphicsBackend.onFrameResized(GraphicsBackend.getWindowHandle(), 800, 600);
	}

	private static void checkEnvironment() {
		// TODO Auto-generated method stub
	}

	private static void initializeGLFW() {
		GLFWErrorCallback.create(new GLFWErrorHandler()::onError).set();

		if (!glfwInit()) {
			throw CrashReports.report(null, "GLFW could not be initialized: glfwInit() has failed");
		}

		GraphicsBackend.setGLFWInitialized(true);
	}

	private static void createWindow() {
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);

		String windowTitle = Progressia.getName() + " " + Progressia.getFullerVersion();
		long handle = glfwCreateWindow(800, 600, windowTitle, NULL, NULL);
		
		if (handle == 0) {
			throw CrashReports.report(null, "Could not create game window");
		}

		GraphicsBackend.setWindowHandle(handle);

		glfwMakeContextCurrent(handle);
		glfwSwapInterval(0); // TODO: remove after config system is added
	}

	private static void positionWindow() {
		// TODO Auto-generated method stub

	}

	private static void createWindowIcons() {
		if (glfwGetPlatform() == GLFW_PLATFORM_WAYLAND) {
			// Wayland does not support changing window icons
			return;
		}
		
		final String prefix = "assets/icons/";

		String[] sizes = ResourceManager.getResource(prefix + "logoSizes.txt").readAsString().split(" ");

		try (GLFWImage.Buffer buffer = GLFWImage.malloc(sizes.length)) {
			for (int i = 0; i < sizes.length; ++i) {
				Resource resource = ResourceManager.getResource(prefix + "logo" + sizes[i].trim() + ".png");
				TextureDataEditor icon = TextureLoader.loadPixels(resource, new TextureSettings(false, true));

				buffer.position(i)
					.width(icon.getContentWidth())
					.height(icon.getContentHeight())
					.pixels(icon.getData().getData());
			}

			glfwSetWindowIcon(GraphicsBackend.getWindowHandle(), buffer);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not load window icons");
		}
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

		glfwSetFramebufferSizeCallback(
			handle,
			GraphicsBackend::onFrameResized
		);

		glfwSetKeyCallback(handle, InputHandler::handleKeyInput);
		glfwSetMouseButtonCallback(
			handle,
			InputHandler::handleMouseButtonInput
		);

		glfwSetCursorPosCallback(handle, InputHandler::handleMouseMoveInput);

		glfwSetScrollCallback(handle, InputHandler::handleWheelScroll);

		GraphicsInterface.subscribeToInputEvents(new Object() {

			@Subscribe
			public void onFrameResized(FrameResizeEvent event) {
				GUI.invalidateEverything();
			}

			@Subscribe
			public void onInputEvent(InputEvent event) {
				GUI.dispatchInput(event);
			}

		});

	}

}
