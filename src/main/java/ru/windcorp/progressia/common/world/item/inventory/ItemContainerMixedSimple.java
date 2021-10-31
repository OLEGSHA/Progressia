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
package ru.windcorp.progressia.common.world.item.inventory;

public class ItemContainerMixedSimple extends ItemContainerMixed {
	
	private final float massLimit;
	private final float volumeLimit;

	public ItemContainerMixedSimple(String id, float massLimit, float volumeLimit) {
		this(id, massLimit, volumeLimit, 1);
	}
	
	public ItemContainerMixedSimple(String id, float massLimit, float volumeLimit, int startingSlots) {
		super(id);
		this.massLimit = massLimit;
		this.volumeLimit = volumeLimit;
		
		addSlots(startingSlots);
	}

	@Override
	public ItemSlot createSlot(int index) {
		return new ItemSlot();
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
