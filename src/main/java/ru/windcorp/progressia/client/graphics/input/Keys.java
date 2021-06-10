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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class Keys {

	private static final TIntObjectMap<String> CODES_TO_NAMES = new TIntObjectHashMap<>();

	private static final TObjectIntMap<String> NAMES_TO_CODES = new TObjectIntHashMap<>();

	private static final TIntSet MOUSE_BUTTONS = new TIntHashSet();

	private static final String KEY_PREFIX = "GLFW_KEY_";
	private static final String MOUSE_BUTTON_PREFIX = "GLFW_MOUSE_BUTTON_";

	private static final Set<String> IGNORE_FIELDS = new HashSet<>(
			Arrays.asList("GLFW_KEY_UNKNOWN", "GLFW_KEY_LAST", "GLFW_MOUSE_BUTTON_LAST", "GLFW_MOUSE_BUTTON_1", // Alias
																												// for
																												// LEFT
					"GLFW_MOUSE_BUTTON_2", // Alias for RIGHT
					"GLFW_MOUSE_BUTTON_3" // Alias for MIDDLE
			));

	static {
		initializeDictionary();
	}

	private static void initializeDictionary() {
		try {

			for (Field field : GLFW.class.getFields()) {
				if (!Modifier.isStatic(field.getModifiers()))
					continue;
				if (!Modifier.isFinal(field.getModifiers()))
					continue;

				String name = field.getName();

				if (!name.startsWith(KEY_PREFIX) && !name.startsWith(MOUSE_BUTTON_PREFIX))
					continue;

				if (IGNORE_FIELDS.contains(name))
					continue;

				addToDictionary(field);
			}

		} catch (IllegalAccessException e) {
			throw CrashReports.report(e, "Cannot access GLFW constants");
		}
	}

	private static void addToDictionary(Field field) throws IllegalAccessException {

		int value = field.getInt(null);
		String name = field.getName();

		if (name.startsWith(KEY_PREFIX)) {
			name = name.substring(KEY_PREFIX.length());
		} else if (name.startsWith(MOUSE_BUTTON_PREFIX)) {
			name = "MOUSE_" + name.substring(MOUSE_BUTTON_PREFIX.length());
			MOUSE_BUTTONS.add(value);
		}

		if (CODES_TO_NAMES.containsKey(value)) {
			throw CrashReports.report(null, "Duplicate keys: %s and %s both map to %d(0x%s)", CODES_TO_NAMES.get(value),
					name, value, Integer.toHexString(value));
		}

		CODES_TO_NAMES.put(value, name);
		NAMES_TO_CODES.put(name, value);
	}

	public static String getInternalName(int code) {
		String result = CODES_TO_NAMES.get(code);

		if (result == null) {
			return "UNKNOWN";
		}

		return result;
	}

	public static String getDisplayName(int code) {
		String name = getInternalName(code);

		if (name.startsWith("KP_")) {
			name = "KEYPAD_" + name.substring("KP_".length());
		}

		name = Character.toTitleCase(name.charAt(0)) + name.substring(1).toLowerCase();

		name = name.replace('_', ' ');

		return name;
	}

	public static int getCode(String internalName) {
		if (NAMES_TO_CODES.containsKey(internalName)) {
			return -1;
		} else {
			return NAMES_TO_CODES.get(internalName);
		}
	}

	public static boolean isMouse(int code) {
		return MOUSE_BUTTONS.contains(code);
	}

}
