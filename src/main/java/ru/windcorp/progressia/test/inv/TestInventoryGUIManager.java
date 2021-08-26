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

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;

public class TestInventoryGUIManager {
	
	private static TestInventoryGUILayer layer;
	
	public static void setup() {
		layer = new TestInventoryGUILayer();
		GUI.addTopLayer(layer);
	}
	
	public static void shutdown() {
		GUI.getLayers().stream().filter(TestInventoryGUILayer.class::isInstance).forEach(GUI::removeLayer);
		layer = null;
	}
	
	public static void openGUI() {
		
		Client client = ClientState.getInstance();
		if (client == null) {
			return;
		}
		
		if (layer == null) {
			return;
		}
		
		EntityDataPlayer entity = client.getLocalPlayer().getEntity();
		if (entity == null) {
			return;
		}
		
		layer.setContainer(entity.getInventory());
		
	}

}
