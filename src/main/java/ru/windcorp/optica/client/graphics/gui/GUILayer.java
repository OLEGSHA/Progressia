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
package ru.windcorp.optica.client.graphics.gui;

import ru.windcorp.optica.client.graphics.flat.AssembledFlatLayer;
import ru.windcorp.optica.client.graphics.flat.RenderTarget;
import ru.windcorp.optica.client.graphics.input.bus.Input;

public abstract class GUILayer extends AssembledFlatLayer {
	
	private final Component root = new Component("Root") {
		protected void handleReassemblyRequest() {
			GUILayer.this.invalidate();
		}
	};

	public GUILayer(String name, Layout layout) {
		super(name);
		getRoot().setLayout(layout);
	}
	
	public Component getRoot() {
		return root;
	}

	@Override
	protected void assemble(RenderTarget target) {
		getRoot().setBounds(0, 0, getWidth(), getHeight());
		getRoot().invalidate();
		getRoot().assemble(target);
	}

	@Override
	protected void handleInput(Input input) {
		getRoot().dispatchInput(input);
	}

}
