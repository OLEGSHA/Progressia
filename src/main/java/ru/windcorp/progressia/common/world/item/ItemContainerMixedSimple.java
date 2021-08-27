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
package ru.windcorp.progressia.common.world.item;

import java.util.ArrayList;
import java.util.List;

public class ItemContainerMixedSimple extends ItemContainerMixed {
	
	private final List<ItemSlot> list;
	
	private final float massLimit;
	private final float volumeLimit;

	public ItemContainerMixedSimple(String id, float massLimit, float volumeLimit) {
		super(id);
		this.list = new ArrayList<>();
		this.massLimit = massLimit;
		this.volumeLimit = volumeLimit;
	}
	
	@Override
	protected List<ItemSlot> getSlots() {
		return list;
	}

	@Override
	public synchronized void addSlots(int amount) {
		((ArrayList<ItemSlot>) list).ensureCapacity(list.size() + amount);
		for (int i = 0; i < amount; ++i) {
			list.add(new ItemSlot());
		}
	}
	
	@Override
	public float getMassLimit() {
		return massLimit;
	}
	
	@Override
	public float getVolumeLimit() {
		return volumeLimit;
	}

}
