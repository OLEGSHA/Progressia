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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

import ru.windcorp.progressia.common.util.crash.CrashReports;

public class KeyMatcher implements Predicate<KeyEvent> {
	
	private static final int ANY_ACTION = -1;

	private final int key;
	private final int mods;
	private final int action;

	protected KeyMatcher(int key, int mods, int action) {
		this.key = key;
		this.mods = mods;
		this.action = action;
	}

	@Override
	public boolean test(KeyEvent event) {
		if (action != ANY_ACTION && event.getAction() != action)
			return false;
		if (event.getKey() != getKey())
			return false;
		if ((event.getMods() & getMods()) != getMods())
			return false;

		return true;
	}

	public int getKey() {
		return key;
	}

	public int getMods() {
		return mods;
	}
	
	public int getAction() {
		return action;
	}

	public static KeyMatcher of(int key) {
		return new KeyMatcher(key, 0, GLFW.GLFW_PRESS);
	}
	
	private static final Map<String, KeyMatcher> RESOLVED_KEYS = Collections.synchronizedMap(new HashMap<>());
	
	public static KeyMatcher of(String glfwConstantName) {
		return RESOLVED_KEYS.computeIfAbsent(glfwConstantName, givenName -> {
			String expectedName = "GLFW_KEY_" + givenName.toUpperCase();
			
			try {
				Field field = GLFW.class.getDeclaredField(expectedName);
				return of(field.getInt(null));
			} catch (NoSuchFieldException e) {
				String hint = "";
				
				if (glfwConstantName.startsWith("GLFW_KEY_")) {
					hint = " (remove prefix \"GLFW_KEY_\")";
				}
				
				throw new IllegalArgumentException("Unknown key constant \"" + glfwConstantName + "\"" + hint);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw CrashReports.report(e, "Could not access GLFW key field {}", expectedName);
			}
		});
	}
	
	public static KeyMatcher ofLeftMouseButton() {
		return new KeyMatcher(GLFW.GLFW_MOUSE_BUTTON_LEFT, 0, GLFW.GLFW_PRESS);
	}
	
	public static KeyMatcher ofRightMouseButton() {
		return new KeyMatcher(GLFW.GLFW_MOUSE_BUTTON_RIGHT, 0, GLFW.GLFW_PRESS);
	}
	
	public static KeyMatcher ofMiddleMouseButton() {
		return new KeyMatcher(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, 0, GLFW.GLFW_PRESS);
	}

	public KeyMatcher with(int modifier) {
		return new KeyMatcher(key, this.mods + modifier, action);
	}

	public KeyMatcher withShift() {
		return with(GLFW.GLFW_MOD_SHIFT);
	}

	public KeyMatcher withCtrl() {
		return with(GLFW.GLFW_MOD_CONTROL);
	}

	public KeyMatcher withAlt() {
		return with(GLFW.GLFW_MOD_ALT);
	}

	public KeyMatcher withSuper() {
		return with(GLFW.GLFW_MOD_SUPER);
	}
	
	public KeyMatcher onRelease() {
		return new KeyMatcher(key, mods, GLFW.GLFW_RELEASE);
	}
	
	public KeyMatcher onRepeat() {
		return new KeyMatcher(key, mods, GLFW.GLFW_REPEAT);
	}
	
	public KeyMatcher onAnyAction() {
		return new KeyMatcher(key, mods, ANY_ACTION);
	}

}
