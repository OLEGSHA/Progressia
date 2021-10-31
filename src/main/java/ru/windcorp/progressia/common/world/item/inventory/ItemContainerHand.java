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
import ru.windcorp.progressia.common.world.entity.SpeciesData.Hand;

public class ItemContainerHand extends ItemContainerSingle {
	
	private static final float HAND_MASS_LIMIT = Units.get("10 kg");
	private static final float HAND_VOLUME_LIMIT = Units.get("5 kg");
	
	private final SpeciesData.Hand hand;

	public ItemContainerHand(String id, Hand hand) {
		super(id, HAND_MASS_LIMIT, HAND_VOLUME_LIMIT);
		this.hand = hand;
	}
	
	public SpeciesData.Hand getHand() {
		return hand;
	}

}
