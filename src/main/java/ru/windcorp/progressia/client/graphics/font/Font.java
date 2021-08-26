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
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.model.Renderable;

public class Font {

	private final Typeface typeface;

	private final int style;
	private final float align;
	private final Vec4 color;
	
	private final int scale;

	public Font(Typeface typeface, int style, float align, Vec4 color, int scale) {
		this.typeface = typeface;
		this.style = style;
		this.align = align;
		this.color = color;
		this.scale = scale;
	}

	public Font(Typeface typeface, int style, float align, int color, int scale) {
		this(typeface, style, align, Colors.toVector(color), scale);
	}

	public Font(Typeface typeface) {
		this(typeface, Typeface.Style.PLAIN, Typeface.ALIGN_LEFT, Colors.WHITE, 2);
	}

	public Font() {
		this(Typefaces.getDefault());
	}

	public Typeface getTypeface() {
		return typeface;
	}

	public int getStyle() {
		return style;
	}

	public float getAlign() {
		return align;
	}

	public Vec4 getColor() {
		return color;
	}
	
	public int getScale() {
		return scale;
	}
	
	private Renderable applyScale(Renderable unscaled) {
		if (scale == 1) {
			return unscaled;
		}
		
		return renderer -> {
			renderer.pushTransform().scale(scale);
			unscaled.render(renderer);
			renderer.popTransform();
		};
	}

	public Renderable assemble(CharSequence chars, float maxWidth) {
		return applyScale(typeface.assembleStatic(chars, style, align, maxWidth, color));
	}

	public Renderable assembleDynamic(Supplier<CharSequence> supplier, float maxWidth) {
		return applyScale(typeface.assembleDynamic(supplier, style, align, maxWidth, color));
	}
	
	public Renderable assemble(CharSequence chars) {
		return assemble(chars, Float.POSITIVE_INFINITY);
	}

	public Renderable assembleDynamic(Supplier<CharSequence> supplier) {
		return assembleDynamic(supplier, Float.POSITIVE_INFINITY);
	}

	public int getWidth(CharSequence chars, float maxWidth) {
		return scale * typeface.getWidth(chars, style, align, maxWidth);
	}

	public int getHeight(CharSequence chars, float maxWidth) {
		return scale * typeface.getHeight(chars, style, align, maxWidth);
	}

	public Vec2i getSize(CharSequence chars, float maxWidth, Vec2i result) {
		result = typeface.getSize(chars, style, align, maxWidth, result);
		result.mul(scale);
		return result;
	}
	
	public int getWidth(CharSequence chars) {
		return getWidth(chars, Float.POSITIVE_INFINITY);
	}

	public int getHeight(CharSequence chars) {
		return getHeight(chars, Float.POSITIVE_INFINITY);
	}

	public Vec2i getSize(CharSequence chars, Vec2i result) {
		return getSize(chars, Float.POSITIVE_INFINITY, result);
	}

	public boolean supports(char c) {
		return typeface.supports(c);
	}

	/**
	 * Creates a new {@link Font} with the specified {@code style} exactly. This
	 * object's style is ignored.
	 * 
	 * @param style the new style
	 * @return the new font
	 */
	public Font withStyle(int style) {
		return new Font(getTypeface(), style, getAlign(), getColor(), getScale());
	}

	public Font deriveBold() {
		return withStyle(getStyle() | Typeface.Style.BOLD);
	}

	public Font deriveItalic() {
		return withStyle(getStyle() | Typeface.Style.ITALIC);
	}

	public Font deriveUnderlined() {
		return withStyle(getStyle() | Typeface.Style.UNDERLINED);
	}

	public Font deriveStrikethru() {
		return withStyle(getStyle() | Typeface.Style.STRIKETHRU);
	}

	public Font deriveShadow() {
		return withStyle(getStyle() | Typeface.Style.SHADOW);
	}

	public Font deriveOutlined() {
		return withStyle(getStyle() | Typeface.Style.OUTLINED);
	}

	public Font deriveNotBold() {
		return withStyle(getStyle() & ~Typeface.Style.BOLD);
	}

	public Font deriveNotItalic() {
		return withStyle(getStyle() & ~Typeface.Style.ITALIC);
	}

	public Font deriveNotUnderlined() {
		return withStyle(getStyle() & ~Typeface.Style.UNDERLINED);
	}

	public Font deriveNotStrikethru() {
		return withStyle(getStyle() & ~Typeface.Style.STRIKETHRU);
	}

	public Font deriveNotShadow() {
		return withStyle(getStyle() & ~Typeface.Style.SHADOW);
	}

	public Font deriveNotOutlined() {
		return withStyle(getStyle() & ~Typeface.Style.OUTLINED);
	}

	public Font withAlign(float align) {
		return new Font(getTypeface(), getStyle(), align, getColor(), getScale());
	}

	public Font withColor(Vec4 color) {
		return new Font(getTypeface(), getStyle(), getAlign(), color, getScale());
	}

	public Font withColor(int color) {
		return new Font(getTypeface(), getStyle(), getAlign(), color, getScale());
	}
	
	public Font withScale(int scale) {
		return new Font(getTypeface(), getStyle(), getAlign(), getColor(), scale);
	}

}
