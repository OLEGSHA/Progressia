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

import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderVertical;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;

public class PermanentHUD extends Component {

	public PermanentHUD(String name, HUDManager manager) {
		super(name);
		setLayout(new LayoutBorderVertical());

		EntityDataPlayer entity = manager.getPlayerEntity();
		if (entity == null) {
			throw new IllegalStateException("Player " + manager.getPlayer() + " does not have an associated entity");
		}
		
		addChild(new HandsHUD(name + ".Hands", manager).setLayoutHint(LayoutBorderVertical.UP));
	}

}
