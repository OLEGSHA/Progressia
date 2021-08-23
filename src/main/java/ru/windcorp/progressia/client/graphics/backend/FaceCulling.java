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

import java.util.ArrayDeque;
import java.util.Deque;

public class FaceCulling {

	private static final Deque<Boolean> STACK = new ArrayDeque<>();

	public static void push(boolean useFaceCulling) {
		GraphicsBackend.setFaceCulling(useFaceCulling);
		STACK.push(Boolean.valueOf(useFaceCulling));
	}

	public static void pop() {
		STACK.pop();

		if (STACK.isEmpty()) {
			GraphicsBackend.setFaceCulling(false);
		} else {
			GraphicsBackend.setFaceCulling(STACK.getFirst());
		}
	}

	private FaceCulling() {
	}

}
