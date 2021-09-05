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

import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.util.Named;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.item.ItemData;

public abstract class SpeciesData extends Namespaced {
	
	public static class Hand extends Named {

		private int index = -1;
		
		public Hand(String name) {
			super(name);
		}
		
		public int getIndex() {
			return index;
		}
		
	}
	
	public static class EquipmentSlot extends Named {
		
		private int index = -1;
		
		private Predicate<ItemData> filter;
		
		public EquipmentSlot(String name, Predicate<ItemData> filter) {
			super(name);
			this.filter = filter;
		}
		
		public int getIndex() {
			return index;
		}
		
		public Predicate<ItemData> getFilter() {
			return filter;
		}
		
	}
	
	private List<Hand> hands;
	private List<EquipmentSlot> equipmentSlots;

	public SpeciesData(String id) {
		super(id);
	}
	
	public void withHands(Hand... hands) {
		if (this.hands != null) {
			throw new IllegalStateException("Hands already set");
		}
		
		if (hands.length == 0) {
			throw new IllegalArgumentException("At least one hand required");
		}
		
		this.hands = ImmutableList.copyOf(hands);
		
		for (int i = 0; i < hands.length; ++i) {
			hands[i].index = i;
		}
	}
	
	public void withEquipmentSlots(EquipmentSlot... equipmentSlots) {
		if (this.equipmentSlots != null) {
			throw new IllegalStateException("Equipment slots already set");
		}
		
		this.equipmentSlots = ImmutableList.copyOf(equipmentSlots);
		
		for (int i = 0; i < equipmentSlots.length; ++i) {
			equipmentSlots[i].index = i;
		}
	}
	
	public List<Hand> getHands() {
		return hands;
	}
	
	public List<EquipmentSlot> getEquipmentSlots() {
		return equipmentSlots;
	}
	
	public abstract CollisionModel getCollisionModel();

	public SpeciesDatalet createDatalet() {
		return new SpeciesDatalet(this);
	}

}
