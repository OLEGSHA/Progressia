/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
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
