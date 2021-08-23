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
import static java.lang.Math.min;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Layout;

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

			Vec2i size = child.getPreferredSize();

			int cWidth = c.getWidth() - 2 * margin;
			int cHeight = c.getHeight() - 2 * margin;

			size.x = min(size.x, cWidth);
			size.y = min(size.y, cHeight);

			child.setBounds(c.getX() + (int) ((cWidth - size.x) * alignX) + margin,
					c.getY() + (int) ((cHeight - size.y) * alignY) + margin, size);

		});
	}

	@Override
	public Vec2i calculatePreferredSize(Component c) {
		Vec2i result = new Vec2i(0, 0);

		c.getChildren().stream().map(child -> child.getPreferredSize()).forEach(size -> {
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
