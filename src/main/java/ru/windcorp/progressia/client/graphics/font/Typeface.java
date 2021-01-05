package ru.windcorp.progressia.client.graphics.font;

import java.util.function.Supplier;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.common.util.Named;
import ru.windcorp.progressia.common.util.Vectors;

public abstract class Typeface extends Named {
	
	public static class Style {
		public static final int
				BOLD       = 1 << 0,
				ITALIC     = 1 << 1,
				UNDERLINED = 1 << 2,
				STRIKETHRU = 1 << 3,
				SHADOW     = 1 << 4;
		
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
	}
	
	public static final float ALIGN_LEFT = 0;
	public static final float ALIGN_RIGHT = 1;
	public static final float ALIGN_CENTER = 0.5f;
	
	public Typeface(String name) {
		super(name);
	}

	public abstract Renderable assemble(
			CharSequence chars, int style,
			float align, float maxWidth,
			int color
	);
	
	public abstract Renderable assembleDynamic(Supplier<CharSequence> supplier, int style, float align, float maxWidth, int color);

	public int getWidth(
			CharSequence chars, int style,
			float align, float maxWidth
	) {
		Vec2i v = Vectors.grab2i();
		v = getSize(chars, style, align, maxWidth, v);
		Vectors.release(v);
		return v.x;
	}
	
	public int getHeight(
			CharSequence chars, int style,
			float align, float maxWidth
	) {
		Vec2i v = Vectors.grab2i();
		v = getSize(chars, style, align, maxWidth, v);
		Vectors.release(v);
		return v.y;
	}
	
	public abstract int getLineHeight();
	
	public abstract Vec2i getSize(
			CharSequence chars, int style,
			float align, float maxWidth,
			Vec2i result
	);
	
	public abstract boolean supports(char c);

}
