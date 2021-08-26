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
import java.util.Collections;
import java.util.List;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;

/**
 * A collection of {@link ItemSlot}s representing a single storage unit. The set
 * of slots is dynamic: new slots may be added and existing slots may be removed
 * at will. A container may impose limits on the maximum total mass and volume
 * of its contents, although this is not enforced on data structure level.
 */
public abstract class ItemContainer extends Namespaced implements Encodable {

	private final List<ItemSlot> synchronizedListView;
	protected final List<ItemSlot> list;

	public ItemContainer(String id, List<ItemSlot> list) {
		super(id);
		this.list = list;
		this.synchronizedListView = Collections.synchronizedList(list);
	}

	/**
	 * Retrieves the modifiable {@link List} of all slots. Edits commissioned
	 * through the returned object update the state of the container.
	 * <p>
	 * It should be assumed that the returned list is
	 * {@linkplain Collections#synchronizedList(List) synchronized}.
	 * 
	 * @return a list view of this container
	 */
	public final List<ItemSlot> getSlots() {
		return this.synchronizedListView;
	}

	/**
	 * Appends additional empty slots to the end of this container.
	 * 
	 * @param amount the amount of slots to add
	 */
	public abstract void addSlots(int amount);

	/**
	 * Computes and returns the mass limit that the container imposes.
	 * 
	 * @return the maximum allowed total mass of the container's contents, or
	 *         {@code Float.POSITIVE_INFINITY} to indicate that no upper
	 *         boundary is set
	 */
	public abstract float getMassLimit();

	/**
	 * Computes and returns the volume limit that the container imposes.
	 * 
	 * @return the maximum allowed total volume of the container's contents, or
	 *         {@code Float.POSITIVE_INFINITY} to indicate that no upper
	 *         boundary is set
	 */
	public abstract float getVolumeLimit();

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
		ItemContainer container = (ItemContainer) destination;
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
