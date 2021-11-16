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

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.events.NewLocalEntityEvent;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.world.item.inventory.InventoryComponent;
import ru.windcorp.progressia.client.world.item.inventory.InventoryRender;
import ru.windcorp.progressia.client.world.item.inventory.InventoryRenderRegistry;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.item.inventory.Inventory;
import ru.windcorp.progressia.common.world.item.inventory.event.InventoryClosingEvent;
import ru.windcorp.progressia.common.world.item.inventory.event.InventoryOpenedEvent;

public class HUDManager implements HUDWorkspace {
	
	private final Client client;
	private final LayerHUD layer;
	
	public HUDManager(Client client) {
		this.client = client;
		this.layer = new LayerHUD(this);
		client.subscribe(new Object() {
			@Subscribe
			public void onLocalEntityChanged(NewLocalEntityEvent e) {
				if (e.getNewEntity() != null) {
					e.getNewEntity().subscribe(HUDManager.this);
				}
				if (e.getPreviousEntity() != null) {
					e.getPreviousEntity().unsubscribe(HUDManager.this);
				}
			}
		});
	}
	
	public void install() {
		GUI.addTopLayer(layer);
	}
	
	public void remove() {
		GUI.removeLayer(layer);
	}
	
	@Override
	public void openInventory(InventoryComponent component) {
		InventoryWindow window = new InventoryWindow("Window", component, this);
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
	
	@Subscribe
	private void onInventoryOpened(InventoryOpenedEvent event) {
		Inventory inventory = event.getInventory();
		InventoryRender render = InventoryRenderRegistry.getInstance().get(inventory.getId());
		
		if (render == null) {
			throw CrashReports.report(null, "InventoryRender not found for ID %s", inventory.getId());
		}
		
		try {
			InventoryComponent component = render.createComponent(inventory, this);
			openInventory(component);
		} catch (Exception e) {
			throw CrashReports.report(e, "Could not open inventory %s", inventory.getId());
		}
	}
	
	@Subscribe
	private void onInventoryClosing(InventoryClosingEvent event) {
		Inventory inventory = event.getInventory();
		
		for (Component component : layer.getWindowManager().getChildren()) {
			if (component instanceof InventoryWindow) {
				InventoryWindow window = (InventoryWindow) component;
				if (window.getContent().getInventory() == inventory) {
					layer.getWindowManager().closeWindow(window);
				}
			}
		}
	}

}
