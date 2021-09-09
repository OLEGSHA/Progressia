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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;

/**
 * An {@link ItemContainer} capable of storing multiple item stacks. The set
 * of slots is dynamic: new slots may be added and existing slots may be removed
 * at will.
 */
public abstract class ItemContainerMixed extends ItemContainer {

	public ItemContainerMixed(String id) {
		super(id);
	}

	/**
	 * Retrieves the modifiable {@link List} of all slots. Edits commissioned
	 * through the returned object update the state of the container.
	 * 
	 * @return a list view of this container
	 */
	protected abstract List<ItemSlot> getSlots();

	/**
	 * Appends additional empty slots to the end of this container.
	 * 
	 * @param amount the amount of slots to add
	 */
	public synchronized void addSlots(int amount) {
		List<ItemSlot> slots = getSlots();
		
		for (int i = 0; i < amount; ++i) {
			ItemSlot slot = createSlot(slots.size());
			slots.add(slot);
			slot.setContainer(this);
		}
	}
	
	/**
	 * Instantiates a new slot object that will be appended to the container. 
	 * 
	 * @param index the index that the new slot will receive
	 * @return the new slot
	 */
	protected abstract ItemSlot createSlot(int index);
	
	@Override
	public ItemSlot getSlot(int index) {
		return getSlots().get(index);
	}
	
	@Override
	public int getSlotCount() {
		return getSlots().size();
	}
	
	@Override
	public void forEach(Consumer<? super ItemSlot> action) {
		getSlots().forEach(action);
	}

	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		List<ItemSlot> slots = getSlots();
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
					getSlots().remove(slots.size() - 1);
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
		List<ItemSlot> slots = getSlots();
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
		List<ItemSlot> mySlots = this.getSlots();
		List<ItemSlot> containerSlots = container.getSlots();

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
						getSlots().remove(containerSlots.size() - 1);
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
