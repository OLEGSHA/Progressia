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

import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;

import java.util.function.Supplier;

public class DynamicLabel extends Component {

	private Font font;
	private Supplier<CharSequence> contents;

	public DynamicLabel(String name, Font font, Supplier<CharSequence> contents, int width) {
		super(name);
		this.font = font;
		this.contents = contents;
		setPreferredSize(width, font.getHeight("", Float.POSITIVE_INFINITY) * 2);
	}

	public Font getFont() {
		return font;
	}

	public Supplier<CharSequence> getContentSupplier() {
		return contents;
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		target.pushTransform(new Mat4().identity().translate(getX(), getY(), -1000).scale(2));
		target.addCustomRenderer(font.assembleDynamic(getContentSupplier(), Float.POSITIVE_INFINITY));
		target.popTransform();
	}

}
