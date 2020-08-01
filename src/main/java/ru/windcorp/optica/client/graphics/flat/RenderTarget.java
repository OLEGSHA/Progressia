package ru.windcorp.optica.client.graphics.flat;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import glm.vec._3.Vec3;
import ru.windcorp.optica.client.graphics.Colors;
import ru.windcorp.optica.client.graphics.backend.Usage;
import ru.windcorp.optica.client.graphics.model.Face;
import ru.windcorp.optica.client.graphics.model.Faces;
import ru.windcorp.optica.client.graphics.model.Shape;
import ru.windcorp.optica.client.graphics.model.WorldRenderable;
import ru.windcorp.optica.client.graphics.texture.Texture;

public class RenderTarget {
	
	private final List<AssembledFlatLayer.Clip> assembled = new ArrayList<>();
	
	private final Deque<Mask> maskStack = new LinkedList<>();
	
	private final List<Face> currentClipFaces = new ArrayList<>();
	
	private int depth = 0;
	
	public RenderTarget() {
		reset();
	}
	
	public void pushMaskStartEnd(int startX, int startY, int endX, int endY) {
		assembleCurrentClipFromFaces();
		maskStack.push(intersect(getMask(), startX, startY, endX, endY));
	}
	
	private Mask intersect(
			Mask current,
			int startX, int startY, int endX, int endY
	) {
		return new Mask(
				Math.max(startX, current.getStartX()),
				Math.max(startY, current.getStartY()),
				Math.min(endX, current.getEndX()),
				Math.min(endY, current.getEndY())
		);
	}
	
	public void pushMask(Mask mask) {
		pushMaskStartEnd(
				mask.getStartX(), mask.getStartY(),
				mask.getEndX(), mask.getEndY()
		);
	}
	
	public void pushMaskStartSize(int x, int y, int width, int height) {
		pushMaskStartEnd(x, y, x + width, y + height);
	}
	
	public void popMask() {
		assembleCurrentClipFromFaces();
		maskStack.pop();
	}

	public Mask getMask() {
		return maskStack.getFirst();
	}
	
	protected void assembleCurrentClipFromFaces() {
		if (!currentClipFaces.isEmpty()) {
			
			Mask mask = getMask();
			if (mask.isEmpty()) {
				currentClipFaces.clear();
				return;
			}
			
			Face[] faces = currentClipFaces.toArray(
					new Face[currentClipFaces.size()]
			);
			currentClipFaces.clear();
			
			Shape shape = new Shape(
					Usage.STATIC, FlatRenderProgram.getDefault(), faces
			);
			
			assembled.add(new AssembledFlatLayer.Clip(mask, shape));
		}
	}
	
	public AssembledFlatLayer.Clip[] assemble() {
		assembleCurrentClipFromFaces();
		
		AssembledFlatLayer.Clip[] result = assembled.toArray(
				new AssembledFlatLayer.Clip[assembled.size()]
		);
		
		reset();
		
		return result;
	}
	
	private void reset() {
		maskStack.clear();
		currentClipFaces.clear();
		assembled.clear();
		
		maskStack.add(new Mask(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
		depth = 0;
	}

	public void addCustomRenderer(WorldRenderable renderable) {
		assembleCurrentClipFromFaces();
		
		Mask mask = getMask();
		if (mask.isEmpty()) {
			return;
		}
		
		assembled.add(new AssembledFlatLayer.Clip(mask, renderable));
	}
	
	protected void addFaceToCurrentClip(Face face) {
		currentClipFaces.add(face);
	}
	
	public void drawTexture(
			int x, int y, int width, int height,
			int color, Texture texture
	) {
		float depth = this.depth--;
		
		addFaceToCurrentClip(Faces.createRectangle(
				FlatRenderProgram.getDefault(),
				texture,
				createVectorFromRGBInt(color),
				new Vec3(x, y + height, depth), // Flip
				new Vec3(width, 0, 0),
				new Vec3(0, -height, 0)
		));
	}
	
	public void drawTexture(
			int x, int y, int width, int height,
			Texture texture
	) {
		drawTexture(x, y, width, height, Colors.WHITE, texture);
	}
	
	public void fill(
			int x, int y, int width, int height,
			int color
	) {
		drawTexture(x, y, width, height, color, null);
	}
	
	private static Vec3 createVectorFromRGBInt(int rgb) {
		int r = (rgb & 0xFF0000) >> 16;
		int g = (rgb & 0x00FF00) >> 8;
		int b = (rgb & 0x0000FF);
		
		return new Vec3(r / 256f, g / 256f, b / 256f);
	}
	
}
