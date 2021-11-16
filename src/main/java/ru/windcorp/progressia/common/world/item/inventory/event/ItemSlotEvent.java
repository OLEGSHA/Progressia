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
package ru.windcorp.progressia.common.world.item.inventory.event;

import ru.windcorp.progressia.common.world.item.inventory.ItemContainer;
import ru.windcorp.progressia.common.world.item.inventory.ItemSlot;

public abstract class ItemSlotEvent extends ItemContainerEvent {
	
	private ItemSlot slot = null;
	private final int index;

	public ItemSlotEvent(ItemContainer container, int index) {
		super(container);
		this.index = index;
	}
	
	public ItemSlotEvent(ItemSlot slot) {
		this(slot.getContainer(), slot.getIndex());
		this.slot = slot;
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return the slot
	 */
	public ItemSlot getSlot() {
		if (slot == null) {
			slot = new ItemSlot(getContainer(), index);
		}
		return slot;
	}

}
