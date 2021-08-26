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
package ru.windcorp.progressia.common.world.entity;

import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.state.ObjectStateField;
import ru.windcorp.progressia.common.world.item.DefaultItemContainer;
import ru.windcorp.progressia.common.world.item.ItemContainer;

public class EntityDataPlayer extends EntityData {
	
	private final ObjectStateField<DefaultItemContainer> inventory = field("Core:Inventory").setShared().def(
		() -> new DefaultItemContainer("Core:PlayerInventory", Units.get(15, "kg"), Units.get(50, "L"))
	).build();

	public EntityDataPlayer(String id) {
		super(id);
		
		setCollisionModel(new AABB(0, 0, 1.8f / 2, 0.8f, 0.8f, 1.8f));
	}
	
	public ItemContainer getInventory() {
		return inventory.get(this);
	}

}
