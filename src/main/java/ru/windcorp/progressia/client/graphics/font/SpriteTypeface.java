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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import glm.mat._4.Mat4;
import glm.vec._2.Vec2;
import glm.vec._2.i.Vec2i;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import gnu.trove.stack.TIntStack;
import gnu.trove.stack.array.TIntArrayStack;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.util.StashingStack;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;

public abstract class SpriteTypeface extends Typeface {

	private final int height;
	private final int thickness;
	private final Vec3 shadowOffset;

	private final TCharObjectMap<Shape> charShapes = new TCharObjectHashMap<>();

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

	@Override
	public int getLineHeight() {
		return getHeight();
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

	public float getDecorativeLineThickness() {
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
	public Vec2i getSize(CharSequence chars, int style, float align, float maxWidth, Vec2i output) {
		if (output == null)
			output = new Vec2i();

		int resultWidth = 0;
		int currentWidth = Style.isBold(style) ? getThickness() : 0;
		int height = getHeight();

		for (int i = 0; i < chars.length(); ++i) {
			char c = chars.charAt(i);

			if (c == '\n' || currentWidth + getWidth(c) > maxWidth) {
				height += getHeight() + getInterlineBuffer();
				if (resultWidth < currentWidth)
					resultWidth = currentWidth;
				currentWidth = Style.isBold(style) ? getThickness() : 0;
			}

			if (c != '\n') {
				currentWidth += getWidth(c);
			}
		}

		if (resultWidth < currentWidth)
			resultWidth = currentWidth;

		return output.set(resultWidth, height);
	}

	private Shape createCharShape(char c) {
		return new Shape(Usage.STATIC, getProgram(), Faces.createRectangle(getProgram(), getTexture(c), Colors.WHITE,
				Vectors.ZERO_3, new Vec3(getWidth(c), 0, 0), new Vec3(0, getHeight(), 0), false));
	}

	private class DynamicText implements Renderable, Drawer {

		private final Supplier<CharSequence> supplier;
		private final int style;
		private final float align;
		private final float maxWidth;
		private final Vec4 color;

		private final Renderable unitLine = new Shape(Usage.STATIC, getProgram(), Faces.createRectangle(getProgram(),
				null, Vectors.UNIT_4, Vectors.ZERO_3, new Vec3(1, 0, 0), new Vec3(0, 1, 0), false));

		private class DynamicWorkspace extends Workspace {
			private ShapeRenderHelper renderer;

			@Override
			public void reset() {
				super.reset();
				renderer = null;
			}
		}

		private final DynamicWorkspace workspace = new DynamicWorkspace();

		public DynamicText(Supplier<CharSequence> supplier, int style, float align, float maxWidth, Vec4 color) {
			this.supplier = supplier;
			this.style = style;
			this.align = align;
			this.maxWidth = maxWidth;
			this.color = color;
		}

		@Override
		public void render(ShapeRenderHelper renderer) {
			CharSequence text = supplier.get();

			renderer.pushTransform().translate(0, +getInterlineBuffer(), 0);

			workspace.renderer = renderer;
			draw(text, this, workspace, style, align, maxWidth, color);

			renderer.popTransform();
		}

		private Shape getShape(char c) {
			Shape shape = charShapes.get(c);
			if (shape == null) {
				shape = createCharShape(c);
				charShapes.put(c, shape);
			}
			return shape;
		}

		/*
		 * Drawer methods
		 */

		@Override
		public void drawChar(char c, Vec4 color, Mat4 transform) {
			workspace.renderer.pushTransform().mul(transform);
			workspace.renderer.pushColorMultiplier().mul(color);
			getShape(c).render(workspace.renderer);
			workspace.renderer.popColorMultiplier();
			workspace.renderer.popTransform();
		}

		@Override
		public void drawRectangle(Vec2 size, Vec4 color, Mat4 transform) {
			workspace.renderer.pushTransform().mul(transform).scale(size.x, size.y, 1);
			workspace.renderer.pushColorMultiplier().mul(color);
			unitLine.render(workspace.renderer);
			workspace.renderer.popColorMultiplier();
			workspace.renderer.popTransform();
		}

	}

	/*
	 * Assembly
	 */

	private class StaticDrawer implements Drawer {

		private class SDWorkspace extends SpriteTypeface.Workspace {

			private final Collection<Face> faces = new ArrayList<>();

			private final Vec3 origin = new Vec3();
			private final Vec3 width = new Vec3();
			private final Vec3 height = new Vec3();

		}

		public final SDWorkspace workspace = new SDWorkspace();

		@Override
		public void drawChar(char c, Vec4 color, Mat4 transform) {
			workspace.origin.set(0, 0, 0);
			workspace.width.set(getWidth(c), 0, 0);
			workspace.height.set(0, getHeight(), 0);

			drawFace(getTexture(c), color, transform);
		}

		@Override
		public void drawRectangle(Vec2 size, Vec4 color, Mat4 transform) {
			workspace.origin.set(0, 0, 0);
			workspace.width.set(size.x, 0, 0);
			workspace.height.set(0, size.y, 0);

			drawFace(null, color, transform);
		}

		private void drawFace(Texture texture, Vec4 color, Mat4 transform) {

			workspace.width.add(workspace.origin);
			workspace.height.add(workspace.origin);

			VectorUtil.applyMat4(workspace.origin, transform);
			VectorUtil.applyMat4(workspace.width, transform);
			VectorUtil.applyMat4(workspace.height, transform);

			workspace.width.sub(workspace.origin);
			workspace.height.sub(workspace.origin);

			workspace.faces.add(Faces.createRectangle(getProgram(), texture, color, workspace.origin, workspace.width,
					workspace.height, false));
		}

		public Renderable assemble() {
			return new Shape(Usage.STATIC, getProgram(), workspace.faces.toArray(new Face[workspace.faces.size()]));
		}

	}

	@Override
	public Renderable assembleStatic(CharSequence text, int style, float align, float maxWidth, Vec4 color) {
		StaticDrawer drawer = new StaticDrawer();
		draw(text, drawer, drawer.workspace, style, align, maxWidth, color);
		return drawer.assemble();
	}

	@Override
	public Renderable assembleDynamic(Supplier<CharSequence> supplier, int style, float align, float maxWidth,
			Vec4 color) {
		return new DynamicText(supplier, style, align, maxWidth, color);
	}

	/*
	 * Drawing algorithm
	 */

	protected static class Workspace {
		private CharSequence text;
		private int fromIndex;
		private int toIndex;

		private final Vec2i totalSize = new Vec2i();

		private float currentWidth;

		private float align;
		private float maxWidth;

		private final TIntStack styles = new TIntArrayStack(16);
		private final StashingStack<Vec4> colors = new StashingStack<>(16, Vec4::new);

		private final Vec2 pos = new Vec2();

		private final StashingStack<Mat4> transforms = new StashingStack<>(16, Mat4::new);

		public Workspace() {
			reset();
		}

		private int pushStyle(int diff) {
			int current = styles.peek();

			if ((diff & 0x10000000) != 0) {
				current &= diff;
			} else {
				current |= diff;
			}

			styles.push(current);
			return current;
		}

		private Vec4 pushColor() {
			if (colors.isEmpty())
				return colors.push();

			Vec4 previous = colors.peek();
			return colors.push().set(previous);
		}

		private Mat4 pushTransform() {
			if (transforms.isEmpty())
				return transforms.push();

			Mat4 previous = transforms.peek();
			return transforms.push().set(previous);
		}

		private Mat4 pushDrawerTransform() {
			Mat4 previous = transforms.peek();
			return transforms.push().identity().translate(pos.x, pos.y, 0).mul(previous);
		}

		public void reset() {
			text = null;
			fromIndex = 0;
			toIndex = 0;
			align = Float.NaN;
			maxWidth = -1;

			totalSize.set(0, 0);

			styles.clear();
			colors.removeAll();

			pos.set(0, 0);

			transforms.removeAll();
			transforms.push().identity();
		}
	}

	protected interface Drawer {
		void drawChar(char c, Vec4 color, Mat4 transform);

		void drawRectangle(Vec2 size, Vec4 color, Mat4 transform);
	}

	protected void draw(CharSequence text, Drawer drawer, Workspace workspace, int style, float align, float maxWidth,
			Vec4 color) {
		workspace.text = text;
		workspace.toIndex = text.length();
		workspace.align = align;
		workspace.maxWidth = maxWidth;

		getSize(text, style, align, maxWidth, workspace.totalSize);

		workspace.styles.push(style);
		workspace.colors.push().set(color);

		drawSpan(drawer, workspace);

		workspace.reset();
	}

	private void drawSpan(Drawer drawer, Workspace workspace) {
		workspace.currentWidth = getBoldOffset();
		workspace.pos.y = workspace.totalSize.y - getHeight();

		int from = workspace.fromIndex;
		int to = workspace.toIndex;

		for (int i = from; i < to; ++i) {
			char c = workspace.text.charAt(i);
			float charWidth = getWidth(c);

			if (c == '\n' || workspace.currentWidth + charWidth > workspace.maxWidth) {

				workspace.pos.x = getStartX(workspace);
				workspace.toIndex = i;
				drawLine(drawer, workspace);

				workspace.pos.y -= getHeight() + getInterlineBuffer();
				workspace.currentWidth = getThickness();

				workspace.fromIndex = i;
				if (c == '\n')
					workspace.fromIndex++; // Skip c
			}

			if (c != '\n')
				workspace.currentWidth += charWidth;
		}

		workspace.pos.x = getStartX(workspace);
		workspace.toIndex = to;
		drawLine(drawer, workspace);

		workspace.currentWidth = Float.NaN;
		workspace.fromIndex = from;
	}

	private float getStartX(Workspace w) {
		return w.align * (w.totalSize.x - w.currentWidth);
	}

	private static final float[][] OUTLINE_DIRECTIONS = new float[][] { { 0, 1 }, { 1, 0 }, { -1, 0 }, { 0, -1 } };

	private void drawLine(Drawer drawer, Workspace workspace) {
		int style = workspace.styles.peek();

		// workspace.pos.x will be restored to this value iff drawLine is
		// invoked multiple times
		float xToRestore = workspace.pos.x;

		if (style == Style.PLAIN) {

			drawPlainLine(drawer, workspace);

		} else if (Style.isOutlined(style)) {

			workspace.pushStyle(~Style.OUTLINED);

			drawLine(drawer, workspace); // TODO figure out why placing this
											// line after drawing outline
											// reverses order of display (should
											// be the opposite)
			workspace.pos.x = xToRestore;

			Colors.multiplyRGB(workspace.pushColor(), getShadowColorMultiplier());

			for (int i = 0; i < OUTLINE_DIRECTIONS.length; ++i) {
				float[] direction = OUTLINE_DIRECTIONS[i];

				workspace.pushTransform().translate(direction[0] * getThickness(), direction[1] * getThickness(), 0);
				drawLine(drawer, workspace);
				workspace.transforms.pop();

				if (i != OUTLINE_DIRECTIONS.length - 1)
					workspace.pos.x = xToRestore;
			}

			workspace.colors.pop();

			workspace.styles.pop();

		} else if (Style.hasShadow(style)) {

			workspace.pushStyle(~Style.SHADOW);

			drawLine(drawer, workspace); // TODO figure out why placing this
											// line after drawing shadow
											// reverses order of display (should
											// be the opposite)
			workspace.pos.x = xToRestore;

			Colors.multiplyRGB(workspace.pushColor(), getShadowColorMultiplier());
			workspace.pushTransform().translate(getShadowOffset());
			drawLine(drawer, workspace);
			workspace.colors.pop();
			workspace.transforms.pop();

			workspace.styles.pop();

		} else if (Style.isBold(style)) {

			workspace.pushStyle(~Style.BOLD);

			workspace.pushTransform().translate(getBoldOffset(), 0, 0);
			drawLine(drawer, workspace);
			workspace.pos.x = xToRestore;
			workspace.transforms.pop();

			drawLine(drawer, workspace);

			workspace.styles.pop();

		} else if (Style.isItalic(style)) {

			workspace.pushStyle(~Style.ITALIC);
			// Push shear of Oy along Ox
			workspace.pushTransform().m10 = getItalicsSlant();

			drawLine(drawer, workspace);

			workspace.transforms.pop();
			workspace.styles.pop();

		} else if (Style.isStrikethru(style)) {

			workspace.pushStyle(~Style.STRIKETHRU);
			drawDecorativeLine(drawer, workspace, (getHeight() - getThickness()) / 2f);
			drawLine(drawer, workspace);
			workspace.styles.pop();

		} else if (Style.isUnderlined(style)) {

			workspace.pushStyle(~Style.UNDERLINED);
			drawDecorativeLine(drawer, workspace, 0);
			drawLine(drawer, workspace);
			workspace.styles.pop();

		} else {
			throw new IllegalArgumentException("Style contains unknown flags " + Integer.toBinaryString(
					(style & ~(Style.BOLD | Style.ITALIC | Style.SHADOW | Style.STRIKETHRU | Style.UNDERLINED))));
		}
	}

	private void drawDecorativeLine(Drawer drawer, Workspace workspace, float height) {
		Vec2 size = Vectors.grab2();

		size.x = workspace.currentWidth;
		size.y = getDecorativeLineThickness();

		drawer.drawRectangle(size, workspace.colors.getHead(), workspace.pushDrawerTransform().translate(0, height, 0));
		workspace.transforms.pop();

		Vectors.release(size);
	}

	private void drawPlainLine(Drawer drawer, Workspace workspace) {
		for (int index = workspace.fromIndex; index < workspace.toIndex; ++index) {
			char c = workspace.text.charAt(index);

			drawer.drawChar(c, workspace.colors.getHead(), workspace.pushDrawerTransform());
			workspace.transforms.pop();

			workspace.pos.x += getWidth(c);
		}
	}

}
