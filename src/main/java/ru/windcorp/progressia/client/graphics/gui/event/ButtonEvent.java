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

package ru.windcorp.progressia.client.graphics.gui.event;

import ru.windcorp.progressia.client.graphics.gui.BasicButton;

public class ButtonEvent extends ComponentEvent {

	public static class Press extends ButtonEvent {
		public Press(BasicButton button) {
			super(button, true);
		}
	}

	public static class Release extends ButtonEvent {
		public Release(BasicButton button) {
			super(button, false);
		}
	}

	private final boolean isPress;

	protected ButtonEvent(BasicButton button, boolean isPress) {
		super(button);
		this.isPress = isPress;
	}

	public static ButtonEvent create(BasicButton button, boolean isPress) {
		if (isPress) {
			return new Press(button);
		} else {
			return new Release(button);
		}
	}

	public boolean isPress() {
		return isPress;
	}

	public boolean isRelease() {
		return !isPress;
	}

}
