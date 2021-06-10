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

import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

public class KeyMatcher {

	private final int key;
	private final int mods;

	protected KeyMatcher(int key, int mods) {
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

	public int getKey() {
		return key;
	}

	public int getMods() {
		return mods;
	}

	public static KeyMatcher.Builder of(int key) {
		return new KeyMatcher.Builder(key);
	}

	public static class Builder {

		private final int key;
		private int mods = 0;

		public Builder(int key) {
			this.key = key;
		}

		public Builder with(int modifier) {
			this.mods += modifier;
			return this;
		}

		public Builder withShift() {
			return with(GLFW.GLFW_MOD_SHIFT);
		}

		public Builder withCtrl() {
			return with(GLFW.GLFW_MOD_CONTROL);
		}

		public Builder withAlt() {
			return with(GLFW.GLFW_MOD_ALT);
		}

		public Builder withSuper() {
			return with(GLFW.GLFW_MOD_SUPER);
		}

		public KeyMatcher build() {
			return new KeyMatcher(key, mods);
		}

		public Predicate<KeyEvent> matcher() {
			return build()::matches;
		}

	}

}
