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

package ru.windcorp.progressia.client.graphics.font;

import java.util.function.Supplier;

import glm.vec._2.i.Vec2i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.common.util.Named;
import ru.windcorp.progressia.common.util.Vectors;

public abstract class Typeface extends Named {

	public static class Style {
		public static final int BOLD = 1 << 0, ITALIC = 1 << 1, UNDERLINED = 1 << 2, STRIKETHRU = 1 << 3,
				SHADOW = 1 << 4, OUTLINED = 1 << 5;

		public static final int PLAIN = 0;

		public static boolean isBold(int style) {
			return (style & BOLD) != 0;
		}

		public static boolean isItalic(int style) {
			return (style & ITALIC) != 0;
		}

		public static boolean isUnderlined(int style) {
			return (style & UNDERLINED) != 0;
		}

		public static boolean isStrikethru(int style) {
			return (style & STRIKETHRU) != 0;
		}

		public static boolean hasShadow(int style) {
			return (style & SHADOW) != 0;
		}

		public static boolean isOutlined(int style) {
			return (style & OUTLINED) != 0;
		}
	}

	public static final float ALIGN_LEFT = 0;
	public static final float ALIGN_RIGHT = 1;
	public static final float ALIGN_CENTER = 0.5f;

	public Typeface(String name) {
		super(name);
	}

	public abstract Renderable assembleStatic(CharSequence chars, int style, float align, float maxWidth, Vec4 color);

	public abstract Renderable assembleDynamic(Supplier<CharSequence> supplier, int style, float align, float maxWidth,
			Vec4 color);

	public int getWidth(CharSequence chars, int style, float align, float maxWidth) {
		Vec2i v = Vectors.grab2i();
		v = getSize(chars, style, align, maxWidth, v);
		Vectors.release(v);
		return v.x;
	}

	public int getHeight(CharSequence chars, int style, float align, float maxWidth) {
		Vec2i v = Vectors.grab2i();
		v = getSize(chars, style, align, maxWidth, v);
		Vectors.release(v);
		return v.y;
	}

	public abstract int getLineHeight();

	public abstract Vec2i getSize(CharSequence chars, int style, float align, float maxWidth, Vec2i result);

	public abstract boolean supports(char c);

}
