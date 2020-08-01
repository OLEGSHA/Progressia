package ru.windcorp.optica.client.graphics.flat;

import ru.windcorp.optica.client.graphics.backend.GraphicsInterface;
import ru.windcorp.optica.client.graphics.model.ShapeRenderHelper;

public class FlatRenderHelper extends ShapeRenderHelper {
	
	private final Mask mask = new Mask();
	
	{
		setupScreenTransform();
	}
	
	public FlatRenderHelper pushMask(
			int startX, int startY,
			int endX, int endY
	) {
		mask.set(startX, startY, endX, endY);
		pushTransform().translate(startX, startY, 0);
		return this;
	}
	
	public FlatRenderHelper pushMask(Mask mask) {
		return pushMask(
				mask.getStartX(), mask.getStartY(),
				mask.getEndX(), mask.getEndY()
		);
	}

	public int getStartX() {
		return mask.getStartX();
	}

	public int getStartY() {
		return mask.getStartY();
	}

	public int getEndX() {
		return mask.getEndX();
	}

	public int getEndY() {
		return mask.getEndY();
	}
	
	public boolean isRenderable() {
		return !mask.isEmpty();
	}

	@Override
	public void reset() {
		super.reset();
		
		setupScreenTransform();
		mask.set(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	private void setupScreenTransform() {
		float width = GraphicsInterface.getFramebufferWidth();
		float height = GraphicsInterface.getFramebufferHeight();
		
		getTransform().translate(-1, +1, 0).scale(2 / width, -2 / height, 1);
	}

}
