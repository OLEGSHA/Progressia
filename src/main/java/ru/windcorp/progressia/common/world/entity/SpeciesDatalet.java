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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.world.entity.SpeciesData.EquipmentSlot;
import ru.windcorp.progressia.common.world.entity.SpeciesData.Hand;
import ru.windcorp.progressia.common.world.item.ItemContainerEquipment;
import ru.windcorp.progressia.common.world.item.ItemContainerHand;

public class SpeciesDatalet implements Encodable {

	private final SpeciesData species;

	private final ItemContainerHand[] hands;
	private final ItemContainerEquipment[] equipment;

	public SpeciesDatalet(SpeciesData species) {
		this.species = species;

		this.hands = new ItemContainerHand[species.getHands().size()];
		for (int i = 0; i < hands.length; ++i) {
			Hand hand = species.getHands().get(i);
			this.hands[i] = new ItemContainerHand(species.getId() + "Hand" + hand.getName(), hand);
		}

		this.equipment = new ItemContainerEquipment[species.getEquipmentSlots().size()];
		for (int i = 0; i < equipment.length; ++i) {
			EquipmentSlot equipmentSlot = species.getEquipmentSlots().get(i);
			this.equipment[i] = new ItemContainerEquipment(
				species.getId() + "EquipmentSlot" + equipmentSlot.getName(),
				equipmentSlot
			);
		}
	}

	public SpeciesData getSpecies() {
		return species;
	}

	@Override
	public void read(DataInput input, IOContext context) throws IOException {
		for (int i = 0; i < hands.length; ++i) {
			hands[i].read(input, context);
		}

		for (int i = 0; i < equipment.length; ++i) {
			equipment[i].read(input, context);
		}
	}

	@Override
	public void write(DataOutput output, IOContext context) throws IOException {
		for (int i = 0; i < hands.length; ++i) {
			hands[i].write(output, context);
		}

		for (int i = 0; i < equipment.length; ++i) {
			equipment[i].write(output, context);
		}
	}

	@Override
	public void copy(Encodable destination) {
		SpeciesDatalet other = (SpeciesDatalet) destination;

		if (other.getSpecies() != getSpecies()) {
			throw new IllegalArgumentException(
				"Cannot copy datalet of species " + other.getSpecies() + " into datalet of species " + getSpecies()
			);
		}
		
		for (int i = 0; i < hands.length; ++i) {
			hands[i].copy(other.hands[i]);
		}

		for (int i = 0; i < equipment.length; ++i) {
			equipment[i].copy(other.equipment[i]);
		}
	}
	
	/**
	 * @return the hands
	 */
	public ItemContainerHand[] getHands() {
		return hands;
	}
	
	/**
	 * @return the equipment
	 */
	public ItemContainerEquipment[] getEquipment() {
		return equipment;
	}

}
