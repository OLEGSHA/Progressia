package ru.windcorp.progressia.client.graphics.font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public abstract class SpriteTypeface extends Typeface {
	
	private final int height;
	private final int thickness;
	private final Vec3 shadowOffset;
	
	public SpriteTypeface(String name, int height, int thinkness) {
		super(name);
		this.height = height;
		this.thickness = thinkness;
		this.shadowOffset = new Vec3(thickness, -thickness, 0);
	}

	public abstract Texture getTexture(char c);
	
	public int getWidth(char c) {
		return getTexture(c).getSprite().getWidth();
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getThickness() {
		return thickness;
	}
	
	public int getInterlineBuffer() {
		return getThickness();
	}
	
	public float getItalicsSlant() {
		return getThickness() / (float) getHeight();
	}
	
	public float getBoldOffset() {
		return getThickness();
	}
	
	public Vec3 getShadowOffset() {
		return shadowOffset;
	}
	
	public float getShadowColorMultiplier() {
		return 0.5f;
	}
	
	public abstract ShapeRenderProgram getProgram();

	@Override
	public WorldRenderable assemble(
			CharSequence chars, int style,
			float align, int maxWidth,
			int color
	) {
		ArrayList<Face> faces = new ArrayList<>();
		
		long packedSize = getSize(chars, style, align, maxWidth);
		int resultWidth = getWidth(packedSize);
		int resultHeight = getHeight(packedSize);
		
		int currentWidth = Style.isBold(style) ? getThickness() : 0;
		int y = resultHeight - getHeight();
		
		int start = 0;
		for (int i = 0; i < chars.length(); ++i) {
			char c = chars.charAt(i);
			
			if (c == '\n' || currentWidth + getWidth(c) > maxWidth) {
				
				assembleLine(
						chars.subSequence(start, i),
						currentWidth, resultWidth, y,
						faces::add,
						style, align, color
				);
				
				y -= getHeight() + getInterlineBuffer();
				
				currentWidth = Style.isBold(style) ? getThickness() : 0;
				
				if (c == '\n') {
					start = i + 1; // Skip c
				} else {
					start = i;     // Don't skip c
				}
			}
			
			if (c != '\n') {
				currentWidth += getWidth(c);
			}
		}
		
		assembleLine(
				chars.subSequence(start, chars.length()),
				currentWidth, resultWidth, y,
				faces::add,
				style, align, color
		);
		
		return new Shape(
				Usage.STATIC, getProgram(),
				faces.toArray(new Face[faces.size()])
		);
	}
	
	private class FaceSpec {
		final Texture texture;
		final Vec3 color;
		
		final Vec3 origin;
		final Vec3 width;
		final Vec3 height;
		
		FaceSpec(
				Texture texture, Vec3 color,
				Vec3 origin, Vec3 width, Vec3 height
		) {
			this.texture = texture;
			this.color = new Vec3(color);
			this.origin = new Vec3(origin);
			this.width = new Vec3(width);
			this.height = new Vec3(height);
		}
		
		Face createFace() {
			return Faces.createRectangle(
					getProgram(),
					texture, color, origin, width, height
			);
		}
		
		FaceSpec copy() {
			return new FaceSpec(texture, color, origin, width, height);
		}
	}

	private void assembleLine(
			CharSequence line,
			int currentWidth, int resultWidth, int y,
			Consumer<Face> output,
			int style, float align, int colorInt
	) {
		List<FaceSpec> faces = new ArrayList<>(line.length() * 2 + 2);
		
		float startX = getStartX(currentWidth, resultWidth, align);
		Vec3 color = createVectorFromRGBInt(colorInt);
		
		specifyCharacters(line, startX, y, color, faces::add);
		
		if (Style.isBold(style)) {
			specifyBold(faces);
		}
		
		if (Style.isUnderlined(style)) {
			specifyUnderline(faces::add, startX, y, currentWidth, color);
		}
		
		if (Style.isStrikethru(style)) {
			specifyStrikethru(faces::add, startX, y, currentWidth, color);
		}
		
		if (Style.isItalic(style)) {
			specifyItalic(faces, y);
		}
		
		if (Style.hasShadow(style)) {
			specifyShadow(faces);
		}
		
		faces.stream().map(FaceSpec::createFace).forEach(output);
	}

	private float getStartX(int currentWidth, int resultWidth, float align) {
		return align * (resultWidth - currentWidth);
	}

	private void specifyCharacters(
			CharSequence line,
			float startX, float y,
			Vec3 color,
			Consumer<FaceSpec> output
	) {
		Vec3 caret = new Vec3(startX, y, 0);
		Vec3 width = new Vec3(0);
		Vec3 height = new Vec3(0, getHeight(), 0);
		
		for (int i = 0; i < line.length(); ++i) {
			char c = line.charAt(i);
			
			Texture texture = getTexture(c);
			float charWidth = getWidth(c);
			
			width.x = charWidth;
			
			output.accept(new FaceSpec(texture, color, caret, width, height));
			
			caret.x += charWidth;
		}
	}

	private void specifyBold(List<FaceSpec> faces) {
		int size = faces.size();
		
		for (int i = 0; i < size; ++i) {
			FaceSpec copy = faces.get(i).copy();
			copy.origin.x += getBoldOffset();
			faces.add(copy);
		}
	}

	private void specifyUnderline(
			Consumer<FaceSpec> output,
			float startX, int y, int currentWidth,
			Vec3 color
	) {
		output.accept(new FaceSpec(
				null,
				color,
				new Vec3(startX, y, 0),
				new Vec3(currentWidth, 0, 0),
				new Vec3(0, getThickness(), 0)
		));
	}

	private void specifyStrikethru(
			Consumer<FaceSpec> output,
			float startX, int y, int currentWidth,
			Vec3 color
	) {
		float startY = y + (getHeight() - getThickness()) / 2f;
		
		output.accept(new FaceSpec(
				null,
				color,
				new Vec3(startX, startY, 0),
				new Vec3(currentWidth, 0, 0),
				new Vec3(0, getThickness(), 0)
		));
	}

	private void specifyItalic(Collection<FaceSpec> faces, int y) {
		for (FaceSpec fs : faces) {
			fs.height.x += getItalicsSlant() * fs.height.y;
			fs.origin.x += getItalicsSlant() * (fs.origin.y - y);
		}
	}

	private void specifyShadow(List<FaceSpec> faces) {
		int size = faces.size();
		
		for (int i = 0; i < size; ++i) {
			FaceSpec copy = faces.get(i).copy();
			
			copy.origin.add(getShadowOffset());
			copy.origin.z = 1;
			copy.color.mul(0.5f);
			
			faces.add(copy);
		}
	}

	@Override
	protected long getSize(
			CharSequence chars, int style,
			float align, int maxWidth
	) {
		int resultWidth = 0;
		int currentWidth = Style.isBold(style) ? getThickness() : 0;
		int height = getHeight();
		
		for (int i = 0; i < chars.length(); ++i) {
			char c = chars.charAt(i);
			
			if (c == '\n' || currentWidth + getWidth(c) > maxWidth) {
				height += getHeight() + getInterlineBuffer();
				if (resultWidth < currentWidth) resultWidth = currentWidth;
				currentWidth = Style.isBold(style) ? getThickness() : 0;
			}
			
			if (c != '\n') {
				currentWidth += getWidth(c);
			}
		}
		
		if (resultWidth < currentWidth) resultWidth = currentWidth;
		
		return pack(resultWidth, height);
	}
	
	// TODO remove
	private static Vec3 createVectorFromRGBInt(int rgb) {
		int r = (rgb & 0xFF0000) >> 16;
		int g = (rgb & 0x00FF00) >> 8;
		int b = (rgb & 0x0000FF);
		
		return new Vec3(r / 256f, g / 256f, b / 256f);
	}

}
