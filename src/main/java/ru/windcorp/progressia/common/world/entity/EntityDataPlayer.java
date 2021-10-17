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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.common.state.IntStateField;
import ru.windcorp.progressia.common.state.ObjectStateField;
import ru.windcorp.progressia.common.util.crash.ReportingEventBus;
import ru.windcorp.progressia.common.world.item.ItemContainerEquipment;
import ru.windcorp.progressia.common.world.item.ItemContainerHand;
import ru.windcorp.progressia.common.world.item.inventory.InventoryClosingEvent;
import ru.windcorp.progressia.common.world.item.inventory.InventoryOpenedEvent;
import ru.windcorp.progressia.common.world.item.inventory.InventoryUser;

public class EntityDataPlayer extends EntityData implements InventoryUser {

	private final ObjectStateField<SpeciesDatalet> speciesDatalet = field("Core:SpeciesDatalet").setShared()
		.of(SpeciesDataRegistry.getInstance().getCodec()).build();
	
	private final IntStateField selectedHand = field("Core:SelectedHand").setShared().ofInt().build();
	
	private final EventBus eventBus = ReportingEventBus.create("EntityDataPlayer");

	public EntityDataPlayer(String id, SpeciesData species) {
		super(id);
		
		setSpecies(species);
	}

	private void setSpecies(SpeciesData species) {
		speciesDatalet.setNow(this, species.createDatalet());
		setCollisionModel(species.getCollisionModel());
	}
	
	public SpeciesData getSpecies() {
		return speciesDatalet.get(this).getSpecies();
	}

	public ItemContainerHand getHand(int index) {
		return speciesDatalet.get(this).getHands()[index];
	}
	
	public int getHandCount() {
		return speciesDatalet.get(this).getHands().length;
	}
	
	public ItemContainerEquipment getEquipmentSlot(int index) {
		return speciesDatalet.get(this).getEquipment()[index];
	}
	
	public int getEquipmentCount() {
		return speciesDatalet.get(this).getEquipment().length;
	}
	
	public int getSelectedHandIndex() {
		return selectedHand.get(this);
	}
	
	public void setSelectedHandIndexNow(int index) {
		selectedHand.setNow(this, index);
	}
	
	public ItemContainerHand getSelectedHand() {
		return getHand(getSelectedHandIndex());
	}
	
	@Subscribe
	private void onInventoryOpened(InventoryOpenedEvent event) {
		eventBus.post(event);
	}
	
	@Subscribe
	private void onInventoryClosed(InventoryClosingEvent event) {
		eventBus.post(event);
	}
	
	public void subscribe(Object listener) {
		eventBus.register(listener);
	}
	
	public void unsubscribe(Object listener) {
		eventBus.unregister(listener);
	}

}
