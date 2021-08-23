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

public class LayoutHorizontal implements Layout {

	private final int margin, gap;

	public LayoutHorizontal(int margin, int gap) {
		this.margin = margin;
		this.gap = gap;
	}

	public LayoutHorizontal(int gap) {
		this(gap, gap);
	}

	public LayoutHorizontal() {
		this(1);
	}

	@Override
	public void layout(Component c) {
		int x = c.getX() + margin, y = c.getY() + margin;

		int width;

		synchronized (c.getChildren()) {
			for (Component child : c.getChildren()) {

				width = child.getPreferredSize().x;
				child.setBounds(x, y, width, c.getHeight() - 2 * margin);
				x += gap + width;

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
					size.x += gap;
				}

				size.y = max(size.y, childPreferredSize.y);
				size.x += childPreferredSize.x;
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
