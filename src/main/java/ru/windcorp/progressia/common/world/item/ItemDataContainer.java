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

public class ItemDataContainer extends ItemData {
	
	private final ObjectStateField<ItemContainer> container = field("Core:Contents").def(this::createContainer).build();
	
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

	protected ItemContainer createContainer() {
		return new ItemContainerMixedSimple(getId(), containerMassLimit, containerVolumeLimit, 10);
	}
	
	public ItemContainer getContainer() {
		return container.get(this);
	}
	
	public boolean canOpenContainer() {
		return true;
	}

	@Override
	public float getMass() {
		return ownMass + getContainer().getMass();
	}

	@Override
	public float getVolume() {
		if (containerContributesVolume) {
			return ownVolume + getContainer().getVolume();
		} else {
			return ownVolume;
		}
	}

}
