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

import ru.windcorp.progressia.common.world.item.inventory.ItemContainer;
import ru.windcorp.progressia.common.world.item.inventory.ItemSlot;

/**
 * A generalization of mass and volume. Not to be extended by mods.
 */
public enum LinearItemProperty {
	
	MASS,
	VOLUME;
	
	public float get(ItemData item) {
		switch (this) {
		case MASS:
			return item.getMass();
		case VOLUME:
			return item.getVolume();
		default:
			throw new AssertionError();
		}
	}
	
	public float get(ItemSlot slot) {
		switch (this) {
		case MASS:
			return slot.getMass();
		case VOLUME:
			return slot.getVolume();
		default:
			throw new AssertionError();
		}
	}
	
	public float get(ItemContainer container) {
		switch (this) {
		case MASS:
			return container.getMass();
		case VOLUME:
			return container.getVolume();
		default:
			throw new AssertionError();
		}
	}
	
	public float getLimit(ItemContainer container) {
		switch (this) {
		case MASS:
			return container.getMassLimit();
		case VOLUME:
			return container.getVolumeLimit();
		default:
			throw new AssertionError();
		}
	}

}
