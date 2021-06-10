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

package ru.windcorp.progressia.client.localization;

import ru.windcorp.jputil.chars.EscapeException;
import ru.windcorp.jputil.chars.Escaper;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Parser {
	private String filePath;
	static private final Escaper ESCAPER = new Escaper.EscaperBuilder().withChars("n", "\n").build();

	public Parser(String filePath) {
		this.filePath = filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Map<String, String> parse() {
		Map<String, String> parsedData = new HashMap<>();
		try (Reader rawData = ResourceManager.getResource(filePath).getReader()) {
			int code;
			char c;
			StringBuilder stringBuilder = new StringBuilder();
			while (true) {
				code = rawData.read();
				if (code == -1) {
					break;
				}
				c = (char) code;
				if (c == '#') {
					while (c != '\n') {
						code = rawData.read();
						if (code == -1) {
							break;
						}
						c = (char) code;
					}
				} else if (c == ' ') {
					code = rawData.read();
					if (code == -1) {
						break;
					}
					c = (char) code;
					if (c == '=') {
						String key = ESCAPER.escape(stringBuilder.toString());
						stringBuilder.setLength(0);
						rawData.read(); // skip a char
						while (true) {
							code = rawData.read();
							if (code == -1) {
								break;
							}

							c = (char) code;
							if (code == '\n') {
								break;
							} else {
								stringBuilder.append(c);
							}
						}
						parsedData.put(ESCAPER.unescape(key), ESCAPER.unescape(stringBuilder.toString()));
						stringBuilder.setLength(0);
					}
				} else if (c == '\n') {
					stringBuilder.setLength(0);
				} else {
					stringBuilder.append(c);
				}
			}

		} catch (IOException | EscapeException e) {
			throw CrashReports.report(e, "Could not parse language file %s", filePath);
		}
		return parsedData;
	}

	public String getFilePath() {
		return filePath;
	}
}
