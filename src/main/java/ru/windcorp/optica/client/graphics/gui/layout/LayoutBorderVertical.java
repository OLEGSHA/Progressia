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

public class LayoutBorderVertical implements Layout {
	
	public static final String
			CENTER = "Center",
			UP = "Up",
			DOWN = "Down";
	
	private final int margin;
	
	public LayoutBorderVertical(int margin) {
		this.margin = margin;
	}
	
	public LayoutBorderVertical() {
		this(1);
	}

	@Override
	public void layout(Component c) {
		int top = 0, bottom = 0;
		
		Size childSize;
		
		synchronized (c.getChildren()) { 
			for (Component child : c.getChildren()) {
				if (child.getLayoutHint() == UP) {
					childSize = child.getPreferredSize();
					top = childSize.height + margin;
					child.setBounds(
							c.getX(),
							c.getY(),
							c.getWidth(),
							childSize.height);
				} else if (child.getLayoutHint() == DOWN) {
					childSize = child.getPreferredSize();
					bottom = childSize.height + margin;
					child.setBounds(
							c.getX(),
							c.getY() + c.getHeight() - childSize.height,
							c.getWidth(), childSize.height);
				}
			}
			
			for (Component child : c.getChildren()) {
				if (child.getLayoutHint() == CENTER) {
					child.setBounds(
							c.getX(),
							c.getY() + top,
							c.getWidth(),
							c.getHeight() - top - bottom);
					
				}
			}
		}
	}

	@Override
	public Size calculatePreferredSize(Component c) {
		Size result = new Size(0, 0);
		int up = 0, down = 0;
		
		Size childSize;
		
		synchronized (c.getChildren()) { 
			for (Component child : c.getChildren()) {
				childSize = child.getPreferredSize();
				if (child.getLayoutHint() instanceof String) {
					
					if (child.getLayoutHint() == UP) {
						up = max(up, childSize.height + margin);
						result.width = max(result.width, childSize.width);
						continue;
					} else if (child.getLayoutHint() == DOWN) {
						down = max(down, childSize.height + margin);
						result.width = max(result.width, childSize.width);
						continue;
					}
					
				}

				result.width = max(result.width, childSize.width);
				result.height = max(result.height, childSize.height);
			}
		}
		result.height += up + down;
		
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + margin + ")";
	}

}
