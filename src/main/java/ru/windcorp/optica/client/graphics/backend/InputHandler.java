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

import com.google.common.eventbus.EventBus;

import glm.vec._2.d.Vec2d;
import ru.windcorp.optica.client.graphics.input.*;

public class InputHandler {
	
	private static final EventBus INPUT_EVENT_BUS = new EventBus("Input");

	// ScrollEvent Start

	private static class ModifiableWheelScrollEvent extends WheelScrollEvent {

		public void initialize(double xoffset, double yoffset) {
			this.xoffset = xoffset;
			this.yoffset = yoffset;
		}

	}

	private static final ModifiableWheelScrollEvent THE_SCROLL_EVENT = new ModifiableWheelScrollEvent();

	static void handleMouseWheel(
			long window,
			double xoffset,
			double yoffset
	) {
		if (GraphicsBackend.getWindowHandle() != window) return;
		THE_SCROLL_EVENT.initialize(xoffset, yoffset);
		dispatch(THE_SCROLL_EVENT);
	}

	// KeyEvent Start
	
	private static class ModifiableKeyEvent extends KeyEvent {
		
		public void initialize(int key, int scancode, int action, int mods) {
			this.key = key;
			this.scancode = scancode;
			this.action = action;
			this.mods = mods;
		}
		
	}

	private static final ModifiableKeyEvent THE_KEY_EVENT = new ModifiableKeyEvent();
	
	static void handleKeyInput(
			long window,
			int key,
			int scancode,
			int action,
			int mods
	) {
		if (GraphicsBackend.getWindowHandle() != window) return;
		THE_KEY_EVENT.initialize(key, scancode, action, mods);
		dispatch(THE_KEY_EVENT);
	}

	// CursorEvent Start
	
	private static class ModifiableCursorMoveEvent extends CursorMoveEvent {
		
		public void initialize(double x, double y) {
			Vec2d newPos = getNewPosition();
			newPos.x = x;
			newPos.y = y;
		}
		
	}
	
	private static final Vec2d CURSOR_POSITION = new Vec2d().set(
			Double.NaN, Double.NaN
	);
	
	private static final ModifiableCursorMoveEvent THE_CURSOR_MOVE_EVENT =
			new ModifiableCursorMoveEvent();
	
	static void handleMouseMoveInput(
			long window,
			double x, double y
	) {
		if (GraphicsBackend.getWindowHandle() != window) return;
		
		if (Double.isNaN(CURSOR_POSITION.x)) {
			CURSOR_POSITION.set(x, y);
		}
		
		THE_CURSOR_MOVE_EVENT.initialize(x, y);
		dispatch(THE_CURSOR_MOVE_EVENT);
		
		CURSOR_POSITION.set(x, y);
	}
	
	public static double getCursorX() {
		return CURSOR_POSITION.x;
	}
	
	public static double getCursorY() {
		return CURSOR_POSITION.y;
	}
	
	public static Vec2d getCursorPosition() {
		return CURSOR_POSITION;
	}
	
	private static void dispatch(InputEvent event) {
		INPUT_EVENT_BUS.post(event);
	}
	
	public static void register(Object listener) {
		INPUT_EVENT_BUS.register(listener);
	}

}
