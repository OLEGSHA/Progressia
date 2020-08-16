package ru.windcorp.progressia.client.graphics.font;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.util.Named;

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

	public abstract WorldRenderable assemble(
			CharSequence chars, int style,
			float align, int maxWidth,
			int color
	);

	public int getWidth(
			CharSequence chars, int style,
			float align, int maxWidth
	) {
		return getWidth(getSize(chars, style, align, maxWidth));
	}
	
	public int getHeight(
			CharSequence chars, int style,
			float align, int maxWidth
	) {
		return getHeight(getSize(chars, style, align, maxWidth));
	}
	
	public Vec2i getSize(
			CharSequence chars, int style,
			float align, int maxWidth,
			Vec2i result
	) {
		if (result == null) {
			result = new Vec2i();
		}
		
		long packed = getSize(chars, style, align, maxWidth);
		result.set(getWidth(packed), getHeight(packed));
		return result;
	}
	
	protected abstract long getSize(
			CharSequence chars, int style,
			float align, int maxWidth
	);
	
	protected static long pack(int width, int height) {
		return CoordinatePacker.pack2IntsIntoLong(width, height);
	}
	
	protected static int getWidth(long packed) {
		return CoordinatePacker.unpack2IntsFromLong(packed, 0);
	}
	
	protected static int getHeight(long packed) {
		return CoordinatePacker.unpack2IntsFromLong(packed, 1);
	}
	
	public abstract boolean supports(char c);

}
