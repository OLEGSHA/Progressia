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

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.graphics.GUI;

public class HUDManager implements HUDWorkspace {
	
	private final Client client;
	private final LayerHUD layer;
	
	public HUDManager(Client client) {
		this.client = client;
		this.layer = new LayerHUD(this);
	}
	
	public void install() {
		GUI.addTopLayer(layer);
	}
	
	public void remove() {
		GUI.removeLayer(layer);
	}
	
	@Override
	public void openContainer(InventoryComponent component) {
		InventoryWindow window = new InventoryWindow("Window", component);
		layer.getWindowManager().addWindow(window);
	}
	
	public void closeEverything() {
		System.err.println("closeEverything NYI");
	}
	
	public boolean isHidden() {
		return layer.isHidden();
	}
	
	public void setHidden(boolean hide) {
		layer.setHidden(hide);
	}
	
	public boolean isInventoryShown() {
		return layer.isInventoryShown();
	}

	public void setInventoryShown(boolean showInventory) {
		layer.setInventoryShown(showInventory);
	}

	@Override
	public Client getClient() {
		return client;
	}

}
