/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.optica.client.graphics.gui.layout;

import static java.lang.Math.max;
import static java.lang.Math.min;

import ru.windcorp.optica.client.graphics.gui.Component;
import ru.windcorp.optica.client.graphics.gui.Layout;
import ru.windcorp.optica.client.graphics.gui.Size;

public class LayoutAlign implements Layout {

	private final int margin;
	private double alignX, alignY;
	
	public LayoutAlign(double alignX, double alignY, int margin) {
		this.alignX = alignX;
		this.alignY = alignY;
		this.margin = margin;
	}
	
	public LayoutAlign(int margin) {
		this(0.5, 0.5, margin);
	}
	
	public LayoutAlign() {
		this(1);
	}

	@Override
	public void layout(Component c) {
		c.getChildren().forEach(child -> {
			
			Size size = child.getPreferredSize();
			
			int cWidth = c.getWidth() - 2 * margin;
			int cHeight = c.getHeight() - 2 * margin;
			
			size.width = min(size.width, cWidth);
			size.height = min(size.height, cHeight);
			
			child.setBounds(
					c.getX() +
						(int) ((cWidth - size.width) * alignX) + margin,
					c.getY() +
						(int) ((cHeight - size.height) * alignY) + margin,
					size
			);
			
		});
	}

	@Override
	public Size calculatePreferredSize(Component c) {
		Size result = new Size(0, 0);
		
		c.getChildren().stream()
			.map(child -> child.getPreferredSize())
			.forEach(size -> {
				result.width = max(size.width, result.width);
				result.height = max(size.height, result.height);
			});
		
		result.width += 2 * margin;
		result.height += 2 * margin;
		
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + margin + ")";
	}

}
