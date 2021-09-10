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

import java.util.ArrayList;
import java.util.Collection;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderVertical;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutGrid;
import ru.windcorp.progressia.common.world.item.ItemContainer;

public class SimpleInventoryComponent extends InventoryComponent {

	private final Group slots = new Group("Inventory.Slots", new LayoutGrid(0, 15));
	private final Collection<InteractiveSlotComponent> slotCollection = new ArrayList<>();
	private final ItemContainer container;

	public SimpleInventoryComponent(ItemContainer container, HUDWorkspace workspace) {
		super("Inventory");
		this.container = container;

		setLayout(new LayoutBorderHorizontal(15));

		Bar massBar = new Bar(
			"MassBar",
			true,
			Colors.toVector(0xFF44AAAA),
			container::getMass,
			container::getMassLimit
		);
		Bar volumeBar = new Bar(
			"VolumeBar",
			false,
			Colors.toVector(0xFFAA4444),
			container::getVolume,
			container::getVolumeLimit
		);

		Component slotsAndVolumeBar = new Group(
			"SlotsAndVolumeBar",
			new LayoutBorderVertical(15),
			slots.setLayoutHint(LayoutBorderVertical.CENTER),
			volumeBar.setLayoutHint(LayoutBorderVertical.UP)
		);

		addChild(slotsAndVolumeBar.setLayoutHint(LayoutBorderHorizontal.CENTER));
		addChild(massBar.setLayoutHint(LayoutBorderHorizontal.LEFT));

		for (int i = 0; i < container.getSlotCount(); ++i) {
			addSlot(container, i, workspace);
		}
	}

	private void addSlot(ItemContainer container, int index, HUDWorkspace workspace) {
		final int maxX = 6;

		InteractiveSlotComponent component = new InteractiveSlotComponent("Inventory.Slot." + index, container, index, workspace);

		Vec2i pos = new Vec2i(index % maxX, index / maxX);
		slots.addChild(component.setLayoutHint(pos));
		slotCollection.add(component);
	}
	
	@Override
	public ItemContainer getContainer() {
		return container;
	}

	@Override
	public Collection<InteractiveSlotComponent> getSlots() {
		return slotCollection;
	}

}
