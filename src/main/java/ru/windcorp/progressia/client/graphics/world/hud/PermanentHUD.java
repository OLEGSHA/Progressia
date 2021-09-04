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
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderVertical;
import ru.windcorp.progressia.client.graphics.texture.SimpleTextures;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.test.inv.SlotComponent;

public class PermanentHUD extends Component {

	public PermanentHUD(String name, LocalPlayer player) {
		super(name);
		setLayout(new LayoutBorderVertical());
		
		EntityDataPlayer entity = player.getEntity();
		if (entity == null) {
			throw new IllegalStateException("Player " + player + " does not have an associated entity");
		}
		
		Group handDisplays = new Group(
			getName() + ".Hands",
			new LayoutBorderHorizontal(),
			new SlotComponent(getName() + ".Hands.LeftHand", entity.getLeftHand(), 0)
				.setBackground(SimpleTextures.get("gui/LeftHand")).setScale(4).setLayoutHint(LayoutBorderHorizontal.LEFT),
			new SlotComponent(getName() + ".Hands.RightHand", entity.getRightHand(), 0)
				.setBackground(SimpleTextures.get("gui/RightHand")).setScale(4).setLayoutHint(LayoutBorderHorizontal.RIGHT)
		);
		
		addChild(handDisplays.setLayoutHint(LayoutBorderVertical.UP));
	}

}
