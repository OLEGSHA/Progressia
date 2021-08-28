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

import java.util.ArrayList;
import java.util.Collection;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutBorderHorizontal;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutGrid;
import ru.windcorp.progressia.common.world.item.ItemContainer;

public class SimpleInventoryComponent extends InventoryComponent {

	private final Group slots = new Group("Inventory.Slots", new LayoutGrid(15));
	private final Collection<DecoratedSlotComponent> slotCollection = new ArrayList<>();

	public SimpleInventoryComponent(ItemContainer container) {
		super("Inventory");

		setLayout(new LayoutBorderHorizontal(15));
		addChild(slots.setLayoutHint(LayoutBorderHorizontal.CENTER));

		for (int i = 0; i < container.getSlotCount(); ++i) {
			addSlot(container, i);
		}
	}

	private void addSlot(ItemContainer container, int index) {
		final int maxX = 6;

		DecoratedSlotComponent component = new DecoratedSlotComponent("Inventory.Slot." + index, container, index);

		Vec2i pos = new Vec2i(index % maxX, index / maxX);
		slots.addChild(component.setLayoutHint(pos));
		slotCollection.add(component);
	}

	@Override
	public Collection<DecoratedSlotComponent> getSlots() {
		return slotCollection;
	}

}
