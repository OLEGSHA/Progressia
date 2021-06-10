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

package ru.windcorp.progressia.client.graphics.flat;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class RenderTarget {

	private static final Mat4 IDENTITY = new Mat4().identity();

	public static class Clip {

		private final MaskStack masks = new MaskStack();
		private final Mat4 transform;
		private final Renderable renderable;

		public Clip(Iterable<TransformedMask> masks, Mat4 transform, Renderable renderable) {
			for (TransformedMask mask : masks) {
				this.masks.pushMask(mask);
			}

			this.transform = transform == null ? IDENTITY : transform;
			this.renderable = Objects.requireNonNull(renderable, "renderable");
		}

		public Mat4 getTransform() {
			return transform;
		}

		public Renderable getRenderable() {
			return renderable;
		}

		public void render(AssembledFlatRenderHelper helper) {
			helper.setMasks(masks.getBuffer());
			helper.pushTransform().mul(getTransform());

			try {
				getRenderable().render(helper);
			} finally {
				helper.popTransform();
				helper.setMasks(null);
			}
		}

	}

	private final List<Clip> assembled = new ArrayList<>();

	private final Deque<TransformedMask> maskStack = new LinkedList<>();
	private final Deque<Mat4> transformStack = new LinkedList<>();
	private final List<Face> currentClipFaces = new ArrayList<>();

	private int depth = 0;

	public RenderTarget() {
		reset();
	}

	protected void assembleCurrentClipFromFaces() {
		if (!currentClipFaces.isEmpty()) {
			Face[] faces = currentClipFaces.toArray(new Face[currentClipFaces.size()]);
			currentClipFaces.clear();

			Shape shape = new Shape(Usage.STATIC, FlatRenderProgram.getDefault(), faces);

			assembled.add(new Clip(maskStack, getTransform(), shape));
		}
	}

	public Clip[] assemble() {
		assembleCurrentClipFromFaces();

		Clip[] result = assembled.toArray(new Clip[assembled.size()]);

		reset();

		return result;
	}

	private void reset() {
		maskStack.clear();
		transformStack.clear();
		currentClipFaces.clear();
		assembled.clear();

		transformStack.add(new Mat4().identity());
		depth = 0;
	}

	public void pushMaskStartEnd(int startX, int startY, int endX, int endY) {
		assembleCurrentClipFromFaces();

		pushTransform(new Mat4().identity().translate(startX, startY, 0));

		maskStack.push(new TransformedMask(new Mask(startX, startY, endX, endY), getTransform()));
	}

	public void pushMask(Mask mask) {
		pushMaskStartEnd(mask.getStartX(), mask.getStartY(), mask.getEndX(), mask.getEndY());
	}

	public void pushMaskStartSize(int x, int y, int width, int height) {
		pushMaskStartEnd(x, y, x + width, y + height);
	}

	public void popMask() {
		assembleCurrentClipFromFaces();
		maskStack.pop();
		popTransform();
	}

	public TransformedMask getMask() {
		return maskStack.getFirst();
	}

	public void pushTransform(Mat4 transform) {
		assembleCurrentClipFromFaces();
		transformStack.push(getTransform().mul(transform, transform));
	}

	public void popTransform() {
		assembleCurrentClipFromFaces();
		transformStack.pop();
	}

	public Mat4 getTransform() {
		return transformStack.getFirst();
	}

	public void addCustomRenderer(Renderable renderable) {
		assembleCurrentClipFromFaces();
		assembled.add(new Clip(maskStack, getTransform(), renderable));
	}

	protected void addFaceToCurrentClip(Face face) {
		currentClipFaces.add(face);
	}

	public void drawTexture(int x, int y, int width, int height, Vec4 color, Texture texture) {
		addFaceToCurrentClip(createRectagleFace(x, y, width, height, color, texture));
	}

	public void drawTexture(int x, int y, int width, int height, int color, Texture texture) {
		drawTexture(x, y, width, height, Colors.toVector(color), texture);
	}

	public void drawTexture(int x, int y, int width, int height, Texture texture) {
		drawTexture(x, y, width, height, Colors.WHITE, texture);
	}

	public void fill(int x, int y, int width, int height, Vec4 color) {
		drawTexture(x, y, width, height, color, null);
	}

	public void fill(int x, int y, int width, int height, int color) {
		fill(x, y, width, height, Colors.toVector(color));
	}

	public void fill(Vec4 color) {
		fill(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE, color);
	}

	public void fill(int color) {
		fill(Colors.toVector(color));
	}

	public Face createRectagleFace(int x, int y, int width, int height, Vec4 color, Texture texture) {
		float depth = this.depth--;

		return Faces.createRectangle(FlatRenderProgram.getDefault(), texture, color, new Vec3(x, y, depth),
				new Vec3(width, 0, 0), new Vec3(0, height, 0), false);
	}

	public Face createRectagleFace(int x, int y, int width, int height, int color, Texture texture) {
		return createRectagleFace(x, y, width, height, Colors.toVector(color), texture);
	}

	public Shape createRectagle(int x, int y, int width, int height, Vec4 color, Texture texture) {
		return new Shape(Usage.STATIC, FlatRenderProgram.getDefault(),
				createRectagleFace(x, y, width, height, color, texture));
	}

	public Shape createRectagle(int x, int y, int width, int height, int color, Texture texture) {
		return createRectagle(x, y, width, height, Colors.toVector(color), texture);
	}

}
