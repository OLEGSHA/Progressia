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

package ru.windcorp.progressia.client.graphics.input;

import org.lwjgl.glfw.GLFW;

public class KeyEvent extends InputEvent {

	protected int key;
	protected int scancode;
	protected int action;
	protected int mods;

	protected KeyEvent(int key, int scancode, int action, int mods, double time) {
		super(time);
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
	}

	public int getKey() {
		return key;
	}

	public int getScancode() {
		return scancode;
	}

	public int getAction() {
		return action;
	}

	public boolean isPress() {
		return action == GLFW.GLFW_PRESS;
	}

	public boolean isRelease() {
		return action == GLFW.GLFW_RELEASE;
	}

	public boolean isRepeat() {
		return action == GLFW.GLFW_REPEAT;
	}

	public boolean isLeftMouseButton() {
		return key == GLFW.GLFW_MOUSE_BUTTON_LEFT;
	}

	public boolean isRightMouseButton() {
		return key == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	}

	public boolean isMiddleMouseButton() {
		return key == GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
	}

	public boolean isMouse() {
		return Keys.isMouse(getKey());
	}

	public int getMods() {
		return mods;
	}

	public boolean hasShift() {
		return (getMods() & GLFW.GLFW_MOD_SHIFT) != 0;
	}

	public boolean hasControl() {
		return (getMods() & GLFW.GLFW_MOD_CONTROL) != 0;
	}

	public boolean hasAlt() {
		return (getMods() & GLFW.GLFW_MOD_ALT) != 0;
	}

	public boolean hasSuper() {
		return (getMods() & GLFW.GLFW_MOD_SUPER) != 0;
	}

	@Override
	public KeyEvent snapshot() {
		return new KeyEvent(key, scancode, action, mods, getTime());
	}

}
