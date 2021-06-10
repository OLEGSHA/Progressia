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

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.EventBus;

import ru.windcorp.progressia.client.graphics.input.*;
import ru.windcorp.progressia.common.util.crash.ReportingEventBus;

public class InputHandler {

	private static final EventBus INPUT_EVENT_BUS = ReportingEventBus.create("Input");

	// KeyEvent

	private static class ModifiableKeyEvent extends KeyEvent {

		protected ModifiableKeyEvent() {
			super(0, 0, 0, 0, Double.NaN);
		}

		public void initialize(int key, int scancode, int action, int mods) {
			this.setTime(GraphicsInterface.getTime());
			this.key = key;
			this.scancode = scancode;
			this.action = action;
			this.mods = mods;
		}

	}

	private static final ModifiableKeyEvent THE_KEY_EVENT = new ModifiableKeyEvent();

	static void handleKeyInput(long window, int key, int scancode, int action, int mods) {
		if (GraphicsBackend.getWindowHandle() != window)
			return;
		THE_KEY_EVENT.initialize(key, scancode, action, mods);
		dispatch(THE_KEY_EVENT);

		switch (action) {
		case GLFW.GLFW_PRESS:
			InputTracker.setKeyState(key, true);
			break;
		case GLFW.GLFW_RELEASE:
			InputTracker.setKeyState(key, false);
			break;
		}
	}

	static void handleMouseButtonInput(long window, int key, int action, int mods) {
		handleKeyInput(window, key, Integer.MAX_VALUE - key, action, mods);
	}

	// CursorMoveEvent

	private static class ModifiableCursorMoveEvent extends CursorMoveEvent {

		protected ModifiableCursorMoveEvent() {
			super(0, 0, Double.NaN);
		}

		public void initialize(double x, double y) {
			this.setTime(GraphicsInterface.getTime());
			getNewPosition().set(x, y);
		}

	}

	private static final ModifiableCursorMoveEvent THE_CURSOR_MOVE_EVENT = new ModifiableCursorMoveEvent();

	static void handleMouseMoveInput(long window, double x, double y) {
		if (GraphicsBackend.getWindowHandle() != window)
			return;
		y = GraphicsInterface.getFrameHeight() - y; // Flip y axis

		InputTracker.initializeCursorPosition(x, y);

		THE_CURSOR_MOVE_EVENT.initialize(x, y);
		dispatch(THE_CURSOR_MOVE_EVENT);

		InputTracker.getCursorPosition().set(x, y);
	}

	// ScrollEvent

	private static class ModifiableWheelScrollEvent extends WheelScrollEvent {

		public ModifiableWheelScrollEvent() {
			super(0, 0, Double.NaN);
		}

		public void initialize(double xOffset, double yOffset) {
			this.setTime(GraphicsInterface.getTime());
			this.getOffset().set(xOffset, yOffset);
		}

	}

	private static final ModifiableWheelScrollEvent THE_WHEEL_SCROLL_EVENT = new ModifiableWheelScrollEvent();

	static void handleWheelScroll(long window, double xoffset, double yoffset) {
		if (GraphicsBackend.getWindowHandle() != window)
			return;
		THE_WHEEL_SCROLL_EVENT.initialize(xoffset, yoffset);
		dispatch(THE_WHEEL_SCROLL_EVENT);
	}

	// FrameResizeEvent

	private static class ModifiableFrameResizeEvent extends FrameResizeEvent {

		public ModifiableFrameResizeEvent() {
			super(0, 0, Double.NaN);
		}

		public void initialize(int width, int height) {
			this.setTime(GraphicsInterface.getTime());
			this.getNewSize().set(width, height);
		}

	}

	private static final ModifiableFrameResizeEvent THE_FRAME_RESIZE_EVENT = new ModifiableFrameResizeEvent();

	/*
	 * NB: this is NOT a GLFW callback, the raw callback is in GraphicsBackend
	 */
	static void handleFrameResize(int width, int height) {
		THE_FRAME_RESIZE_EVENT.initialize(width, height);
		dispatch(THE_FRAME_RESIZE_EVENT);
	}

	// Misc

	private static void dispatch(InputEvent event) {
		INPUT_EVENT_BUS.post(event);
	}

	public static void register(Object listener) {
		INPUT_EVENT_BUS.register(listener);
	}

}
