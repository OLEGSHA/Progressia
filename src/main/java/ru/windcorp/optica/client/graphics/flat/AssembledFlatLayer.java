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

public abstract class AssembledFlatLayer extends Layer {

	private final AssembledFlatRenderHelper helper =
			new AssembledFlatRenderHelper();
	
	private final RenderTarget target = new RenderTarget();
	
	private boolean needsReassembly = true;
	private RenderTarget.Clip[] clips = null;
	
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
		
		for (RenderTarget.Clip clip : clips) {
			clip.render(helper);
		}
		
		helper.reset();
	}
	
}
