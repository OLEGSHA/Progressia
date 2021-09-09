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

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.world.entity.SpeciesRender;
import ru.windcorp.progressia.client.world.entity.SpeciesRenderRegistry;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.SpeciesData.EquipmentSlot;

public class InventoryHUD extends Component {

	public enum Side {
		LEFT("Left", LayoutBorderHorizontal.LEFT),
		RIGHT("Right", LayoutBorderHorizontal.RIGHT);

		private final String ccName;
		private final Object lbhHint;

		private Side(String ccName, Object lbhHint) {
			this.ccName = ccName;
			this.lbhHint = lbhHint;
		}
	}

	public InventoryHUD(String name, HUDWorkspace workspace) {
		super(name);
		setLayout(new LayoutBorderHorizontal());

		EntityDataPlayer entity = workspace.getPlayerEntity();
		String speciesId = entity.getSpecies().getId();
		SpeciesRender speciesRender = SpeciesRenderRegistry.getInstance().get(speciesId);

		Map<Side, Component> containers = Maps.toMap(
			Arrays.asList(Side.values()),
			side -> new Group(name + "." + side.ccName, new LayoutVertical(15))
		);

		for (int i = 0; i < entity.getEquipmentCount(); ++i) {

			EquipmentSlot slot = entity.getSpecies().getEquipmentSlots().get(i);

			SlotComponent display = new SlotComponent(name + "." + slot.getName(), entity.getEquipmentSlot(i), 0)
				.setBackground(speciesRender.getEquipmentSlotBackground(slot))
				.setScale(2);

			InteractiveSlotComponent interactiveDisplay = new InteractiveSlotComponent(
				display,
				workspace
			);

			containers.get(speciesRender.getEquipmentSlotSide(slot)).addChild(interactiveDisplay);

		}

		containers.forEach((side, comp) -> {
			addChild(
				new Group(name + "." + side.ccName + ".Aligner", new LayoutAlign(0, 1, 0), comp)
					.setLayoutHint(side.lbhHint)
			);
		});
	}

}
