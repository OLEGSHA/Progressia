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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;

/**
 * A collection of {@link ItemSlot}s representing a single storage unit. A
 * container may impose limits on the maximum total mass and volume of its
 * contents, although this is not enforced on data structure level.
 * <p>
 * At any moment container has a definite amount of slots, each identified by a
 * unique index. If a container has <i>n</i> slots, its slots are numbered 0
 * through <i>n</i> - 1.
 */
public abstract class ItemContainer extends Namespaced implements Encodable, Iterable<ItemSlot> {

	public ItemContainer(String id) {
		super(id);
	}

	/**
	 * Retrieves the slot with the given index.
	 * 
	 * @param index the index of the slot to retrieve
	 * @return the slot or {@code null} if the slot does not exist
	 */
	public abstract ItemSlot getSlot(int index);

	/**
	 * Returns the current slot count of this container.
	 * 
	 * @return the number of slots in this container
	 */
	public abstract int getSlotCount();
	
	@Override
	public abstract void forEach(Consumer<? super ItemSlot> action);
	
	@Override
	public Iterator<ItemSlot> iterator() {
		return new Iterator<ItemSlot>() {
			
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getSlotCount();
			}

			@Override
			public ItemSlot next() {
				if (!hasNext()) {
					throw new NoSuchElementException("index = " + index + ", size = " + getSlotCount());
				}
				ItemSlot slot = getSlot(index);
				index++;
				return slot;
			}
			
		};
	}

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
	
	public synchronized float getMass() {
		float sum = 0;
		for (int i = 0; i < getSlotCount(); ++i) {
			ItemSlot slot = getSlot(i);
			
			if (slot.isEmpty()) {
				continue;
			}
			
			sum += slot.getContents().getMass() * slot.getAmount();
		}
		return sum;
	}
	
	public synchronized float getVolume() {
		float sum = 0;
		for (int i = 0; i < getSlotCount(); ++i) {
			ItemSlot slot = getSlot(i);
			
			if (slot.isEmpty()) {
				continue;
			}
			
			sum += slot.getContents().getVolume() * slot.getAmount();
		}
		return sum;
	}

}
