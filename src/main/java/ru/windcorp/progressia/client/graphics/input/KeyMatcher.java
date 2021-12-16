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

import java.util.Map;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableMap;

public class KeyMatcher {
	
	private static final Pattern DECLAR_SPLIT_REGEX = Pattern.compile("\\s*\\+\\s*");
	private static final Map<String, Integer> MOD_TOKENS = ImmutableMap.of(
		"SHIFT", GLFW.GLFW_MOD_SHIFT,
		"CONTROL", GLFW.GLFW_MOD_CONTROL,
		"ALT", GLFW.GLFW_MOD_ALT,
		"SUPER", GLFW.GLFW_MOD_SUPER
	);
	
	public static final KeyMatcher LMB = new KeyMatcher(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	public static final KeyMatcher RMB = new KeyMatcher(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	public static final KeyMatcher MMB = new KeyMatcher(GLFW.GLFW_MOUSE_BUTTON_MIDDLE);

	private final int key;
	private final int mods;

	public KeyMatcher(int key, int mods) {
		this.key = key;
		this.mods = mods;
	}
	
	public KeyMatcher(int key) {
		this.key = key;
		this.mods = 0;
	}
	
	public KeyMatcher(String declar) {
		String[] tokens = DECLAR_SPLIT_REGEX.split(declar);
		if (tokens.length == 0) {
			throw new IllegalArgumentException("No tokens found in \"" + declar + "\"");
		}
		
		int key = -1;
		int mods = 0;
		
		for (String token : tokens) {
			token = token.toUpperCase();
			
			if (MOD_TOKENS.containsKey(token)) {
				int mod = MOD_TOKENS.get(token);
				if ((mods & mod) != 0) {
					throw new IllegalArgumentException("Duplicate modifier \"" + token + "\" in \"" + declar + "\"");
				}
				mods |= mod;
			} else if (key != -1) {
				throw new IllegalArgumentException("Too many non-modifier tokens in \"" + declar + "\": maximum one key, first offender: \"" + token + "\"");
			} else {
				token = token.replace(' ', '_');
				
				if (token.startsWith("KEYPAD_")) {
					token = "KP_" + token.substring("KEYPAD_".length());
				}
			
				key = Keys.getCode(token);
				
				if (key == -1) {
					throw new IllegalArgumentException("Unknown token \"" + token + "\" in \"" + declar + "\"");
				}
			}
		}
		
		this.key = key;
		this.mods = mods;
	}

	public boolean matches(KeyEvent event) {
		if (!event.isPress())
			return false;
		if (event.getKey() != getKey())
			return false;
		if ((event.getMods() & getMods()) != getMods())
			return false;

		return true;
	}
	
	public boolean matchesIgnoringAction(KeyEvent event) {
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
	
	public KeyMatcher with(int modifier) {
		return new KeyMatcher(key, mods | modifier);
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

}
