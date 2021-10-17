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

import ru.windcorp.progressia.common.state.ObjectStateField;
import ru.windcorp.progressia.common.world.item.inventory.InventorySimple;
import ru.windcorp.progressia.common.world.item.inventory.InventoryUser;

public class ItemDataContainer extends ItemData {

	private final ObjectStateField<InventorySimple> inventory = field("Core:Contents").setShared().def(this::createInventory)
		.build();

	private final float ownMass;
	private final float containerMassLimit;

	private final float ownVolume;
	private final float containerVolumeLimit;
	private final boolean containerContributesVolume;

	public ItemDataContainer(
		String id,
		float ownMass,
		float containerMassLimit,
		float ownVolume,
		float containerVolumeLimit,
		boolean containerContributesVolume
	) {
		super(id);
		this.ownMass = ownMass;
		this.containerMassLimit = containerMassLimit;
		this.ownVolume = ownVolume;
		this.containerVolumeLimit = containerVolumeLimit;
		this.containerContributesVolume = containerContributesVolume;
	}

	protected InventorySimple createInventory() {
		return new InventorySimple(
			getId(),
			new ItemContainerMixedSimple(getId(), containerMassLimit, containerVolumeLimit, 10)
		);
	}
	
	public InventorySimple getInventory() {
		return inventory.get(this);
	}

	public boolean isOpen() {
		return !getInventory().getUsers().isEmpty();
	}

	public boolean canOpen(InventoryUser user) {
		return true;
	}

	public synchronized InventorySimple open(InventoryUser user) {
		if (isOpen()) {
			return null;
		} else if (!canOpen(user)) {
			return null;
		} else {
			getInventory().open(user);
			return getInventory();
		}
	}

	public synchronized void close() {
		getInventory().closeAll();
	}

	@Override
	public float getMass() {
		return ownMass + getInventory().getMass();
	}

	@Override
	public float getVolume() {
		if (containerContributesVolume) {
			return ownVolume + getInventory().getVolume();
		} else {
			return ownVolume;
		}
	}

}
