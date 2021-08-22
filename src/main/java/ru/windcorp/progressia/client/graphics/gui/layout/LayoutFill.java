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
 
package ru.windcorp.progressia.client.graphics.gui.layout;

import static java.lang.Math.max;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Layout;

public class LayoutFill implements Layout {

	private final int margin;
	
	public LayoutFill(int margin) {
		this.margin = margin;
	}

	public LayoutFill() {
		this(0);
	}

	@Override
	public void layout(Component c) {
		c.getChildren().forEach(child -> {

			int cWidth = c.getWidth() - 2 * margin;
			int cHeight = c.getHeight() - 2 * margin;

			child.setBounds(
				c.getX() + margin,
				c.getY() + margin,
				cWidth,
				cHeight
			);

		});
	}

	@Override
	public Vec2i calculatePreferredSize(Component c) {
		Vec2i result = new Vec2i(0, 0);

		c.getChildren().stream()
			.map(child -> child.getPreferredSize())
			.forEach(size -> {
				result.x = max(size.x, result.x);
				result.y = max(size.y, result.y);
			});

		result.x += 2 * margin;
		result.y += 2 * margin;

		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + margin + ")";
	}

}
