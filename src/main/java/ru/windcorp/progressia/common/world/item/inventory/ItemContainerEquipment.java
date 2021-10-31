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

import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.entity.SpeciesData;

public class ItemContainerEquipment extends ItemContainerSingle {
	
	private static final float EQUIP_MASS_LIMIT = Units.get("15 kg");
	private static final float EQUIP_VOLUME_LIMIT = Units.get("60 kg");
	
	private final SpeciesData.EquipmentSlot equipmentSlot;

	public ItemContainerEquipment(String id, SpeciesData.EquipmentSlot equipmentSlot) {
		super(id, EQUIP_MASS_LIMIT, EQUIP_VOLUME_LIMIT);
		this.equipmentSlot = equipmentSlot;
	}
	
	public SpeciesData.EquipmentSlot getEquipmentSlot() {
		return equipmentSlot;
	}

}
