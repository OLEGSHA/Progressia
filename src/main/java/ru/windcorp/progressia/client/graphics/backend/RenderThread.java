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

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import ru.windcorp.progressia.client.audio.AudioManager;
import ru.windcorp.progressia.client.graphics.GUI;

class RenderThread extends Thread {

	public RenderThread() {
		super("Render");
	}

	@Override
	public void run() {
		LWJGLInitializer.initialize();
		mainLoop();
		freeResources();
	}

	private void mainLoop() {
		while (shouldRun()) {
			GraphicsBackend.startFrame();
			RenderTaskQueue.runTasks();
			render();
			waitForFrame();
			GraphicsBackend.endFrame();
		}

		System.exit(0);
	}

	private void render() {
		clear();
		doRender();
		glfwPollEvents();
	}

	private void clear() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private void doRender() {
		GUI.render();
		AudioManager.update();
	}

	private void waitForFrame() {
		glfwSwapBuffers(GraphicsBackend.getWindowHandle());
	}

	private void freeResources() {
		OpenGLObjectTracker.deleteAllObjects();
	}

	private boolean shouldRun() {
		return !glfwWindowShouldClose(GraphicsBackend.getWindowHandle());
	}

}
