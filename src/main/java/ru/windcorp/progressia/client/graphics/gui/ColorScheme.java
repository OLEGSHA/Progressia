/*
 * Progressia
 * Copyright (C)  2020-2022  Wind Corporation and contributors
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
package ru.windcorp.progressia.client.graphics.gui;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import glm.vec._4.Vec4;
import ru.windcorp.jputil.SyntaxException;
import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.namespaces.IllegalIdException;
import ru.windcorp.progressia.common.util.namespaces.NamespacedUtil;

public class ColorScheme {
	
	private static Map<String, Vec4> defaultScheme;
	
	public static Vec4 get(String key) {
		Vec4 color = defaultScheme.get(key);
		if (color == null) {
			throw CrashReports.report(null, "ColorScheme does not contain color %s", key);
		}
		return color;
	}
	
	public static void load(Resource resource) {
		Map<String, Vec4> tmpMap = new HashMap<>();
		
		try {
			for (String fullLine : StringUtil.split(resource.readAsString(), '\n')) {
				String line = fullLine.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				
				String[] parts = StringUtil.split(line, '=', 2);
				if (parts[1] == null) {
					throw new SyntaxException("Could not parse \"" + line + "\": '=' not found");
				}
				
				String key = parts[0].trim();
				checkKeyName(key);
				if (tmpMap.containsKey(key)) {
					throw new SyntaxException("Duplicate key " + key);
				}
				
				String value = parts[1].trim();
				Vec4 color;
				
				if (value.startsWith("#")) {
					color = parseValueAsHex(value);
				} else if (value.startsWith("$")) {
					color = parseValueAsReference(value, tmpMap);
				} else {
					throw new SyntaxException("Unknown value format \"" + value + "\"");
				}
				
				tmpMap.put(key, color);
			}
		} catch (SyntaxException e) {
			throw CrashReports.report(e, "Could not load ColorScheme from %s", resource);
		}
		
		defaultScheme = ImmutableMap.copyOf(tmpMap);
	}
	
	private static void checkKeyName(String key) throws SyntaxException {
		try {
			NamespacedUtil.checkId(key);
		} catch (IllegalIdException e) {
			throw new SyntaxException("Illegal key name \"" + key + "\"", e);
		}
	}
	
	private static int parseHex(String from, int start, int length) {
		return Integer.parseInt(from.substring(start, start + length), 0x10);
	}

	private static Vec4 parseValueAsHex(String value) throws SyntaxException {
		int a = 0xFF;
		int r, g, b;
		
		switch (value.length() - 1) {
		case 3:
			// #RGB
			r = parseHex(value, 1, 1) * 0x11;
			g = parseHex(value, 2, 1) * 0x11;
			b = parseHex(value, 3, 1) * 0x11;
			break;
		case 4:
			// #ARGB
			a = parseHex(value, 1, 1) * 0x11;
			r = parseHex(value, 2, 1) * 0x11;
			g = parseHex(value, 3, 1) * 0x11;
			b = parseHex(value, 4, 1) * 0x11;
			break;
		case 6:
			// #RRGGBB
			r = parseHex(value, 1, 2);
			g = parseHex(value, 3, 2);
			b = parseHex(value, 5, 2);
			break;
		case 8:
			// #AARRGGBB
			a = parseHex(value, 1, 2);
			r = parseHex(value, 3, 2);
			g = parseHex(value, 5, 2);
			b = parseHex(value, 7, 2);
			break;
		default:
			throw new SyntaxException("Could not parse hex color \"" + value + "\": expecting #RGB, #ARGB, #RRGGBB or #AARRGGBB");
		}
		
		return new Vec4(r / 255.0, g / 255.0, b / 255.0, a / 255.0);
	}
	
	private static Vec4 parseValueAsReference(String value, Map<String, Vec4> map) throws SyntaxException {
		String otherKey = value.substring(1);
		checkKeyName(otherKey);
		if (!map.containsKey(otherKey)) {
			throw new SyntaxException("Key $" + otherKey + " not found");
		}
		return map.get(otherKey);
	}

}
