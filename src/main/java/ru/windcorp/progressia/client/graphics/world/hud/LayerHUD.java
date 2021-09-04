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
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;

public class LayerHUD extends GUILayer {

	public LayerHUD(Client client) {
		super("LayerHUD", new LayoutFill(15));
		setCursorPolicy(CursorPolicy.INDIFFERENT);

		client.subscribe(this);
	}
	
	@Subscribe
	private void onEntityChanged(NewLocalEntityEvent e) {
		while (!getRoot().getChildren().isEmpty()) {
			getRoot().removeChild(getRoot().getChild(0));
		}
		
		if (e.getNewEntity() == null) {
			return;
		}
		
		getRoot().addChild(new PermanentHUD(getName(), e.getClient().getLocalPlayer()));
		getRoot().requestReassembly();
	}

}
