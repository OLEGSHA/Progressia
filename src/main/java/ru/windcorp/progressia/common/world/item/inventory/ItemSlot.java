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

import ru.windcorp.progressia.common.world.item.ItemData;

/**
 * A reference to a slot in a container. The container and the index of an
 * {@code ItemSlot} cannot be changed.
 * <p>
 * {@code ItemSlot}s are wrapper objects; there may be multiple objects
 * referencing a single slot. Slot objects are considered
 * {@linkplain #equals(Object) equal} iff their indices are equal and they refer
 * to the same container.
 * <p>
 * This class provides public methods for fetching slot contents but not for
 * changing them. To alter a slot, use an appropriate method from {@link Items}.
 */
public class ItemSlot {

	private final ItemContainer container;
	private final int index;

	public ItemSlot(ItemContainer container, int index) {
		this.container = container;
		this.index = index;
	}

	/**
	 * @return the container
	 */
	public ItemContainer getContainer() {
		return container;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	public Inventory getInventory() {
		return container.getInventory();
	}

	public ItemData getItem() {
		return container.getItem(index);
	}

	public int getCount() {
		return container.getCount(index);
	}

	public boolean isEmpty() {
		return container.isEmpty(index);
	}

	public boolean canAdd(ItemData item, int count) {
		return container.canAdd(index, item, count);
	}

	public boolean canRemove(ItemData item, int count) {
		return container.canRemove(index, item, count);
	}

	protected boolean add(ItemData item, int count) {
		return container.add(index, item, count);
	}

	protected boolean remove(ItemData item, int count) {
		return container.remove(index, item, count);
	}

	public float getMass() {
		synchronized (container) {
			int count = getCount();
			if (count == 0) {
				return 0;
			}
			return count * getItem().getMass();
		}
	}

	public float getVolume() {
		synchronized (container) {
			int count = getCount();
			if (count == 0) {
				return 0;
			}
			return count * getItem().getVolume();
		}
	}

	/*
	 * For purposes of equality checking, all container instances are considered
	 * different
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + System.identityHashCode(container);
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemSlot other = (ItemSlot) obj;
		if (container != other.container)
			return false;
		if (index != other.index)
			return false;
		return true;
	}

}
