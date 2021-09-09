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
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.item.ItemContainerHand;

public interface HUDWorkspace {
	
	Client getClient();
	
	default LocalPlayer getPlayer() {
		return getClient().getLocalPlayer();
	}
	
	default EntityDataPlayer getPlayerEntity() {
		return getClient().getLocalPlayer().getEntity();
	}
	
	default ItemContainerHand getHand() {
		return getClient().getLocalPlayer().getEntity().getSelectedHand();
	}
	
	void openContainer(InventoryComponent component);

}
