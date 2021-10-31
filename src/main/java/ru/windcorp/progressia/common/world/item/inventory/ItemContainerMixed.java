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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.world.item.inventory.event.ItemSlotAddedEvent;
import ru.windcorp.progressia.common.world.item.inventory.event.ItemSlotChangedEvent;
import ru.windcorp.progressia.common.world.item.inventory.event.ItemSlotRemovedEvent;

/**
 * An {@link ItemContainer} capable of storing multiple item stacks. The set
 * of slots is dynamic: new slots may be added and existing slots may be removed
 * at will.
 */
public abstract class ItemContainerMixed extends ItemContainer {
	
	private final List<ItemSlot> slots = new ArrayList<>();

	public ItemContainerMixed(String id) {
		super(id);
	}
	
	@Override
	protected void setInventory(Inventory inventory) {
		if (getInventory() != null) {
			getInventory().unsubscribe(this);
		}
		
		super.setInventory(inventory);
		
		if (getInventory() != null) {
			getInventory().subscribe(this);
		}
	}

	/**
	 * Appends additional empty slots to the end of this container.
	 * 
	 * @param amount the amount of slots to add
	 */
	public synchronized void addSlots(int amount) {
		Inventory inventory = getInventory();
		
		for (int i = 0; i < amount; ++i) {
			ItemSlot slot = createSlot(slots.size());
			slots.add(slot);
			slot.setContainer(this);
			
			if (inventory != null) {
				inventory.getEventBus().post(new ItemSlotAddedEvent(slot));
			}
		}
	}
	
	/**
	 * Instantiates a new slot object that will be appended to the container. 
	 * 
	 * @param index the index that the new slot will receive
	 * @return the new slot
	 */
	protected abstract ItemSlot createSlot(int index);
	
	@Subscribe
	private void onSlotChanged(ItemSlotChangedEvent e) {
		cleanUpSlots();
	}
	
	public void cleanUpSlots() {
		Inventory inventory = getInventory();
		Collection<ItemSlotRemovedEvent> events = null;
		
		// Do not remove slot 0
		for (int i = slots.size() - 1; i > 0; --i) {
			ItemSlot slot = slots.get(i);
			if (slot.isEmpty()) {
				slots.remove(i);
				
				if (inventory != null) {
					if (events == null) {
						events = new ArrayList<>(slots.size() - i);
					}
					events.add(new ItemSlotRemovedEvent(slot));
				}
			} else {
				break;
			}
		}
		
		if (events != null) {
			// events != null only if inventory != null 
			events.forEach(inventory.getEventBus()::post);
		}
	}
	
	@Override
	public ItemSlot getSlot(int index) {
		if (index < 0 || index >= slots.size()) {
			return null;
		}
		return slots.get(index);
	}
	
	@Override
	public int getSlotCount() {
		return slots.size();
	}
	
	@Override
	public void forEach(Consumer<? super ItemSlot> action) {
		slots.forEach(action);
	}

	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		synchronized (slots) {

			int needSlots = input.readInt();
			int hasSlots = slots.size();

			int costOfResetting = needSlots;
			int costOfEditing = Math.abs(needSlots - hasSlots);

			if (costOfResetting < costOfEditing) {
				slots.clear();
				addSlots(needSlots);
			} else {
				while (slots.size() > needSlots) {
					slots.remove(slots.size() - 1);
				}

				if (slots.size() < needSlots) {
					addSlots(needSlots - slots.size());
				}
			}

			for (int i = 0; i < needSlots; ++i) {
				slots.get(i).read(input, context);
			}

		}
	}

	@Override
	public synchronized void write(DataOutput output, IOContext context) throws IOException {
		synchronized (slots) {

			output.writeInt(slots.size());
			for (int i = 0; i < slots.size(); ++i) {
				slots.get(i).write(output, context);
			}

		}
	}

	@Override
	public void copy(Encodable destination) {
		ItemContainerMixed container = (ItemContainerMixed) destination;
		List<ItemSlot> mySlots = this.slots;
		List<ItemSlot> containerSlots = container.slots;

		synchronized (mySlots) {
			synchronized (containerSlots) {

				int needSlots = mySlots.size();
				int hasSlots = containerSlots.size();

				int costOfResetting = needSlots;
				int costOfEditing = Math.abs(needSlots - hasSlots);

				if (costOfResetting < costOfEditing) {
					containerSlots.clear();
					container.addSlots(needSlots);
				} else {
					while (containerSlots.size() > needSlots) {
						slots.remove(containerSlots.size() - 1);
					}

					if (containerSlots.size() < needSlots) {
						addSlots(needSlots - containerSlots.size());
					}
				}

				for (int i = 0; i < needSlots; ++i) {
					mySlots.get(i).copy(containerSlots.get(i));
				}

			}
		}
	}

}
