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

package ru.windcorp.progressia.client.graphics.gui;

import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.Colors;

public class Button extends BasicButton {

	public Button(String name, String label, Font labelFont) {
		super(name, label, labelFont);
	}
	
	public Button(String name, String label) {
		this(name, label, new Font());
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		// Border
		
		Vec4 borderColor;
		if (isPressed() || isHovered() || isFocused()) {
			borderColor = Colors.BLUE;
		} else {
			borderColor = Colors.LIGHT_GRAY;
		}
		target.fill(getX(), getY(), getWidth(), getHeight(), borderColor);
		
		// Inside area
		
		if (isPressed()) {
			// Do nothing
		} else {
			Vec4 backgroundColor;
			if (isHovered() && isEnabled()) {
				backgroundColor = Colors.HOVER_BLUE;
			} else {
				backgroundColor = Colors.WHITE;
			}
			target.fill(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, backgroundColor);
		}
		
		// Change label font color
		
		if (isPressed()) {
			getLabel().setFont(getLabel().getFont().withColor(Colors.WHITE));
		} else {
			getLabel().setFont(getLabel().getFont().withColor(Colors.BLACK));
		}
	}
	
	@Override
	protected void postAssembleSelf(RenderTarget target) {
		// Apply disable tint
		
		if (!isEnabled()) {
			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.toVector(0x88FFFFFF));
		}
	}
}
