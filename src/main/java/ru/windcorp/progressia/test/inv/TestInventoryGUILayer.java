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
package ru.windcorp.progressia.test.inv;

import org.lwjgl.glfw.GLFW;

import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.item.ItemContainerMixed;

public class TestInventoryGUILayer extends GUILayer {

	private ItemContainerMixed container;

	private InventoryScreen display = null;

	public TestInventoryGUILayer() {
		super("Inventory", new LayoutFill(0));
		setCursorPolicy(CursorPolicy.INDIFFERENT);
	}

	public void setContainer(ItemContainerMixed container, EntityDataPlayer player) {
		this.container = container;

		getRoot().removeChild(display);
		display = null;

		if (container != null) {
			display = new InventoryScreen("Screen", new InventoryComponent(container), player);

			getRoot().addChild(display);
			invalidate();

			setCursorPolicy(CursorPolicy.REQUIRE);
		} else {
			setCursorPolicy(CursorPolicy.INDIFFERENT);
		}

		GUI.updateLayer(this);
	}

	@Override
	protected void doRender() {
		if (container != null) {
			super.doRender();
		}
	}

	@Override
	protected void handleInput(Input input) {
		if (container != null) {
			if (!input.isConsumed()) {
				InputEvent event = input.getEvent();

				if (event instanceof KeyEvent) {
					KeyEvent keyEvent = (KeyEvent) event;
					if (
						keyEvent.isPress()
							&& (keyEvent.getKey() == GLFW.GLFW_KEY_E || keyEvent.getKey() == GLFW.GLFW_KEY_ESCAPE)
					) {
						setContainer(null, null);
						input.consume();
					}
				}
			}

			super.handleInput(input);
			input.consume();
		}
	}

}
