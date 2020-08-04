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

import ru.windcorp.optica.client.graphics.gui.Component;
import ru.windcorp.optica.client.graphics.gui.Layout;
import ru.windcorp.optica.client.graphics.gui.Size;

public class LayoutBorderHorizontal implements Layout {
	
	public static final String CENTER = "Center",
			LEFT = "Left",
			RIGHT = "Right";
	
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
		
		Size childSize;
		
		synchronized (c.getChildren()) { 
			for (Component child : c.getChildren()) {
				if (child.getLayoutHint() == LEFT) {
					childSize = child.getPreferredSize();
					left = childSize.width + margin;
					child.setBounds(
							c.getX(),
							c.getY(),
							childSize.width,
							c.getHeight());
				} else if (child.getLayoutHint() == RIGHT) {
					childSize = child.getPreferredSize();
					right = childSize.width + margin;
					child.setBounds(
							c.getX() + c.getWidth() - childSize.width,
							c.getY(),
							childSize.width,
							c.getHeight());
				}
			}
			
			for (Component child : c.getChildren()) {
				if (child.getLayoutHint() == CENTER) {
					child.setBounds(
							c.getX() + left,
							c.getY(),
							c.getWidth() - left - right,
							c.getHeight());
					
				}
			}
		}
	}

	@Override
	public Size calculatePreferredSize(Component c) {
		Size result = new Size(0, 0);
		int left = 0, right = 0;
		
		Size childSize;
		
		synchronized (c.getChildren()) { 
			for (Component child : c.getChildren()) {
				childSize = child.getPreferredSize();
				if (child.getLayoutHint() instanceof String) {
					
					if (child.getLayoutHint() == LEFT) {
						left = max(left, childSize.width + margin);
						result.height = max(result.height, childSize.height);
						continue;
					} else if (child.getLayoutHint() == RIGHT) {
						right = max(right, childSize.width + margin);
						result.height = max(result.height, childSize.height);
						continue;
					}
					
				}

				result.width = max(result.width, childSize.width);
				result.height = max(result.height, childSize.height);
			}
		}
		result.width += left + right;
		
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + margin + ")";
	}

}
