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
package ru.windcorp.progressia.client.graphics.world.hud;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.events.NewLocalEntityEvent;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Components;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.test.TestPlayerControls;

public class LayerHUD extends GUILayer {

	private final HUDManager manager;
	private WindowedHUD windowManager = null;

	private boolean showInventory = false;
	private boolean isHidden = false;

	public LayerHUD(HUDManager manager) {
		super("LayerHUD", new LayoutFill(15));
		this.manager = manager;

		setCursorPolicy(CursorPolicy.INDIFFERENT);

		manager.getClient().subscribe(this);
	}

	@Subscribe
	private void onEntityChanged(NewLocalEntityEvent e) {
		while (!getRoot().getChildren().isEmpty()) {
			getRoot().removeChild(getRoot().getChild(0));
		}

		if (e.getNewEntity() == null) {
			getRoot().requestReassembly();
			return;
		}

		getRoot().addChild(new PermanentHUD(getName() + ".Permanent", manager));

		Component inventoryGroup = new Group(getName() + ".InventoryGroup", new LayoutFill());
		
		inventoryGroup.addChild(new InventoryHUD(getName() + ".Equipment", manager));
		
		windowManager = new WindowedHUD(getName() + ".Windows");
		inventoryGroup.addChild(windowManager);

		inventoryGroup.addChild(new CursorHUD(getName() + ".Cursor", manager.getPlayerEntity()));
		
		getRoot().addChild(Components.hide(inventoryGroup, () -> !showInventory));

		getRoot().requestReassembly();
	}
	
	public WindowedHUD getWindowManager() {
		return windowManager;
	}

	public boolean isInventoryShown() {
		return showInventory;
	}

	public void setInventoryShown(boolean showInventory) {
		this.showInventory = showInventory;
		updateCursorPolicy();
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
		updateCursorPolicy();
	}

	private void updateCursorPolicy() {
		if (showInventory && !isHidden) {
			setCursorPolicy(CursorPolicy.REQUIRE);
		} else {
			setCursorPolicy(CursorPolicy.INDIFFERENT);
		}

		GUI.updateLayer(this);
	}

	@Override
	protected void handleInput(Input input) {
		if (isHidden) {
			return;
		}

		super.handleInput(input);

		if (showInventory) {
			TestPlayerControls.getInstance().handleCtrlIfApplicable(input);
			if (!input.isConsumed()) {
				handleCloseInventoryIfApplicable(input);
			}
			input.consume();
		}
	}

	private void handleCloseInventoryIfApplicable(Input input) {
		if (input.getEvent() instanceof KeyEvent) {
			KeyEvent event = (KeyEvent) input.getEvent();

			if (event.isPress() && (event.getKey() == GLFW.GLFW_KEY_E || event.getKey() == GLFW.GLFW_KEY_ESCAPE)) {
				setInventoryShown(false);
				input.consume();
			}
		}
	}

	@Override
	protected void doRender() {
		if (isHidden) {
			return;
		}

		super.doRender();
	}

}
