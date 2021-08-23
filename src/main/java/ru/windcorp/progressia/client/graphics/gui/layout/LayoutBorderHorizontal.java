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

public class LayoutBorderHorizontal implements Layout {

	public static final String CENTER = "Center", LEFT = "Left", RIGHT = "Right";

	private final int margin;

	public LayoutBorderHorizontal(int margin) {
		this.margin = margin;
	}

	public LayoutBorderHorizontal() {
		this(1);
	}

	@Override
	public void layout(Component c) {
		int left = 0, right = 0;

		Vec2i childSize;

		synchronized (c.getChildren()) {
			for (Component child : c.getChildren()) {
				if (child.getLayoutHint() == LEFT) {
					childSize = child.getPreferredSize();
					left = childSize.x + margin;
					child.setBounds(c.getX(), c.getY(), childSize.x, c.getHeight());
				} else if (child.getLayoutHint() == RIGHT) {
					childSize = child.getPreferredSize();
					right = childSize.x + margin;
					child.setBounds(c.getX() + c.getWidth() - childSize.x, c.getY(), childSize.x, c.getHeight());
				}
			}

			for (Component child : c.getChildren()) {
				if (child.getLayoutHint() == CENTER) {
					child.setBounds(c.getX() + left, c.getY(), c.getWidth() - left - right, c.getHeight());

				}
			}
		}
	}

	@Override
	public Vec2i calculatePreferredSize(Component c) {
		Vec2i result = new Vec2i(0, 0);
		int left = 0, right = 0;

		Vec2i childSize;

		synchronized (c.getChildren()) {
			for (Component child : c.getChildren()) {
				childSize = child.getPreferredSize();
				if (child.getLayoutHint() instanceof String) {

					if (child.getLayoutHint() == LEFT) {
						left = max(left, childSize.x + margin);
						result.y = max(result.y, childSize.y);
						continue;
					} else if (child.getLayoutHint() == RIGHT) {
						right = max(right, childSize.x + margin);
						result.y = max(result.y, childSize.y);
						continue;
					}

				}

				result.x = max(result.x, childSize.x);
				result.y = max(result.y, childSize.y);
			}
		}
		result.x += left + right;

		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + margin + ")";
	}

}
