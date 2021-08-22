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

import java.util.Objects;

import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;

public class Panel extends Group {

	private Vec4 fill;
	private Vec4 border;

	public Panel(String name, Layout layout, Vec4 fill, Vec4 border) {
		super(name, layout);
		
		this.fill = Objects.requireNonNull(fill, "fill");
		this.border = border;
	}
	
	public Panel(String name, Layout layout) {
		this(name, layout, Colors.WHITE, Colors.LIGHT_GRAY);
	}
	
	/**
	 * @return the fill
	 */
	public Vec4 getFill() {
		return fill;
	}
	
	/**
	 * @param fill the fill to set
	 */
	public void setFill(Vec4 fill) {
		this.fill = Objects.requireNonNull(fill, "fill");
	}
	
	/**
	 * @return the border
	 */
	public Vec4 getBorder() {
		return border;
	}
	
	/**
	 * @param border the border to set
	 */
	public void setBorder(Vec4 border) {
		this.border = border;
	}
	
	@Override
	protected void assembleSelf(RenderTarget target) {
		if (border == null) {
			target.fill(getX(), getY(), getWidth(), getHeight(), fill);
		} else {
			target.fill(getX(), getY(), getWidth(), getHeight(), border);
			target.fill(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, fill);
		}
	}

}
