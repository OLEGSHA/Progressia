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

public class LayoutVertical implements Layout {

	private final int margin, gap;

	public LayoutVertical(int margin, int gap) {
		this.margin = margin;
		this.gap = gap;
	}

	public LayoutVertical(int gap) {
		this(gap, gap);
	}

	public LayoutVertical() {
		this(1);
	}

	@Override
	public void layout(Component c) {
		int x = c.getX() + margin, y = c.getY() + c.getHeight();

		synchronized (c.getChildren()) {
			for (Component child : c.getChildren()) {

				int height = child.getPreferredSize().y;
				y -= gap + height;
				child.setBounds(x, y, c.getWidth() - 2 * margin, height);

			}
		}
	}

	@Override
	public Vec2i calculatePreferredSize(Component c) {
		Vec2i size = new Vec2i(0, 0);
		Vec2i childPreferredSize;

		synchronized (c.getChildren()) {
			for (int i = 0; i < c.getChildren().size(); ++i) {
				childPreferredSize = c.getChild(i).getPreferredSize();

				if (i > 0) {
					size.y += gap;
				}

				size.x = max(size.x, childPreferredSize.x);
				size.y += childPreferredSize.y;
			}
		}

		size.x += 2 * margin;
		size.y += 2 * margin;

		return size;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + gap + ", " + margin + ")";
	}

}
