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
import ru.windcorp.progressia.common.world.item.ItemContainerMixedSimple;
import ru.windcorp.progressia.common.world.item.ItemContainerHand;
import ru.windcorp.progressia.common.world.item.ItemContainerMixed;

public class EntityDataPlayer extends EntityData {
	
	private final ObjectStateField<ItemContainerMixedSimple> inventory = field("Core:Inventory").setShared().def(
		() -> new ItemContainerMixedSimple("Core:PlayerInventory", Units.get(15, "kg"), Units.get(50, "L"))
	).build();
	
	private final ObjectStateField<ItemContainerHand> leftHand = field("Core:LeftHand").setShared().def(
		() -> new ItemContainerHand("Core:PlayerLeftHand", Units.get(10, "kg"), Units.get(5, "L"))
	).build();
	
	private final ObjectStateField<ItemContainerHand> rightHand = field("Core:RightHand").setShared().def(
		() -> new ItemContainerHand("Core:PlayerRightHand", Units.get(10, "kg"), Units.get(5, "L"))
	).build();

	public EntityDataPlayer(String id) {
		super(id);
		setCollisionModel(new AABB(0, 0, 1.8f / 2, 0.8f, 0.8f, 1.8f));
	}
	
	public ItemContainerMixed getInventory() {
		return inventory.get(this);
	}
	
	public ItemContainerHand getLeftHand() {
		return leftHand.get(this);
	}
	
	public ItemContainerHand getRightHand() {
		return rightHand.get(this);
	}

}
