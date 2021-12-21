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
package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.BooleanSupplier;

import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.model.Renderable;

public class Hider extends Component {

	private final BooleanSupplier shouldHide;
	private final Component contents;

	public Hider(String name, Component contents, BooleanSupplier shouldHide) {
		super(name);
		this.contents = contents;
		this.shouldHide = shouldHide;
		
		setLayout(new LayoutFill());
		addChild(contents);
	}
	
	@Override
	protected boolean passInputToChildren(InputEvent e) {
		return !shouldHide.getAsBoolean();
	}
	
	@Override
	public synchronized Component findFocused() {
		if (shouldHide.getAsBoolean()) {
			return null;
		}
		
		return super.findFocused();
	}
	
	@Override
	protected void assembleChildren(RenderTarget target) {
		Renderable renderable = contents.assembleToRenderable();
		target.addCustomRenderer(renderer -> {
			if (!shouldHide.getAsBoolean()) {
				renderable.render(renderer);
			}
		});
	}

}
