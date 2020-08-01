package ru.windcorp.optica.client.graphics.flat;

import ru.windcorp.optica.client.graphics.Layer;
import ru.windcorp.optica.client.graphics.model.WorldRenderable;

public abstract class AssembledFlatLayer extends Layer {

	private final FlatRenderHelper helper = new FlatRenderHelper();
	
	private final RenderTarget target = new RenderTarget();
	
	private boolean needsReassembly = true;
	private Clip[] clips = null;
	
	public AssembledFlatLayer(String name) {
		super(name);
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	public void markForReassembly() {
		needsReassembly = true;
	}
	
	private void doReassemble() {
		assemble(target);
		clips = target.assemble();
		needsReassembly = false;
	}
	
	protected abstract void assemble(RenderTarget target);
	
	@Override
	protected void doRender() {
		if (needsReassembly) {
			doReassemble();
		}
		
		for (Clip clip : clips) {
			clip.render(helper);
		}
		
		helper.reset();
	}
	
	public static class Clip {
		
		private final Mask mask = new Mask();
		private final WorldRenderable renderable;

		public Clip(
				int startX, int startY,
				int endX, int endY,
				WorldRenderable renderable
		) {
			mask.set(startX, startY, endX, endY);
			this.renderable = renderable;
		}
		
		public Clip(
				Mask mask,
				WorldRenderable renderable
		) {
			this(
					mask.getStartX(), mask.getStartY(),
					mask.getEndX(), mask.getEndY(),
					renderable
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

		public WorldRenderable getRenderable() {
			return renderable;
		}
		
		public void render(FlatRenderHelper helper) {
			helper.pushMask(getStartX(), getStartY(), getEndX(), getEndY());
			renderable.render(helper);
			helper.popTransform();
		}
		
	}
	
}
