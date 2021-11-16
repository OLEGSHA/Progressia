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

import java.util.Iterator;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemDataWithContainers;
import ru.windcorp.progressia.common.world.item.inventory.event.ItemSlotChangedEvent;

/**
 * The base class of item containers. An item container is a set of slots with
 * defined limits on mass and volume. Containers are typically grouped into an
 * {@link Inventory} by in-game function.
 * <p>
 * A container contains some number of slots indexed from 0. Users may inspect
 * the contents of any slot. Slots may contain an {@link ItemData} and an item
 * count. Item count is zero iff the {@link ItemData} is {@code null}, in which
 * case the slot is considered empty.
 * <p>
 * Item containers also implement mass and volume calculation. Both properties
 * are determined by the corresponding {@link ItemData} methods, and combine
 * linearly:
 * {@code mass(2 * stick + 5 * apple) = 2 * mass(stick) + 5 * mass(apple)},
 * empty slots do not contribute any mass of volume. Users may query these
 * computed values. In addition, a limit may be imposed on both properties.
 * Containers will refuse operations which would lead to the violation of these
 * limits. However, containers cannot detect excess mass or volume resulting
 * from
 * a implementation-triggered change of limits.
 * <p>
 * As a safeguard against memory leaks, all containers must be provided with a
 * maximum possible size. Item containers will refuse operations on slots with
 * indices exceeding or equal to the maximum possible size.
 * <p>
 * Implementors will typically find the following subclasses useful:
 * <ul>
 * <li>{@link ItemContainerMixed} for containers allowing an arbitrary amount of
 * slots
 * <ul>
 * <li>see {@link ItemContainerMixedSimple} for a complete implementation</li>
 * </ul>
 * </li>
 * <li>{@link ItemContainerSingle} for containers providing only one slot</li>
 * </ul>
 */
public abstract class ItemContainer extends Namespaced implements Encodable {

	private Inventory inventory;
	private final int maxPossibleSize;

	private final TIntSet subContainersCache = new TIntHashSet();

	public ItemContainer(String id, int maxPossibleSize) {
		super(id);
		this.maxPossibleSize = maxPossibleSize;
	}

	/**
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * @param inventory the inventory to set
	 */
	void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	/**
	 * Retrieves the item in the slot at the specified index.
	 * 
	 * @param index the index of the slot to query
	 * @return the item or {@code null} if the slot is empty.
	 */
	public abstract ItemData getItem(int index);

	/**
	 * Retrieves the item count in the slot at the specified index.
	 * 
	 * @param index the index of the slot to query
	 * @return the item count or {@code 0} if the slot is empty
	 */
	public abstract int getCount(int index);

	/**
	 * Returns a slot index strictly greater than all indices containing
	 * items. Use this index as upper bound when iterating the container's
	 * items. The slot at this index is empty.
	 * 
	 * @return a strict upper bound on the indices of filled slots
	 */
	public abstract int getMaxIndex();

	/**
	 * Computes and returns the slot index of the last non-empty slot in this
	 * container. This operation is potentially slower than
	 * {@link #getMaxIndex()}, which is less precise.
	 * 
	 * @return the index of the last filled slot, or -1 if no such slot exists
	 */
	public int getLastFilledSlot() {
		int index = getMaxIndex();
		while (isEmpty(index) && index >= 0) {
			index--;
		}
		return index;
	}

	/**
	 * Determines whether items of the specified kind could be inserted into
	 * this container assuming a slot is available and mass and volume
	 * requirements of the container are satisfied.
	 * <p>
	 * This method should only be a function of the configuration of the
	 * container and of the item; it must not depend on the contents of the
	 * container.
	 * 
	 * @param item the item to check, not null
	 * @return {@code true} iff the item is allowed in this container
	 */
	protected boolean canAdd(ItemData item) {
		if (item == null) {
			return false;
		}
		
		if (containsRecursively(item, this)) {
			return false;
		}

		return true;
	}

	private boolean containsRecursively(ItemData haystack, ItemContainer needle) {
		if (!(haystack instanceof ItemDataWithContainers)) {
			return false;
		}
		
		ItemDataWithContainers haystackWithTastyHay = (ItemDataWithContainers) haystack;
		
		Iterator<? extends ItemContainer> iterator = haystackWithTastyHay.getAllContainers();
		while (iterator.hasNext()) {
			ItemContainer container = iterator.next();
			
			if (container == needle) {
				return true;
			}
			
			TIntIterator indexIterator = container.subContainersCache.iterator();
			
			while (indexIterator.hasNext()) {
				
				int subHaystackIndex = indexIterator.next();
				ItemData subHaystackOrMaybeNot = container.getItem(subHaystackIndex);
				if (containsRecursively(subHaystackOrMaybeNot, needle)) {
					return true;
				}
				
			}
		}
		
		return false;
	}

	/**
	 * Determines whether inventory users are allowed to manually remove items
	 * from this container.
	 * <p>
	 * This method should only be a function of the configuration of the
	 * container; it must not depend on the contents of the container.
	 * 
	 * @return {@code true} iff items could be removed from this container
	 */
	protected boolean isRemovingAllowed() {
		return true;
	}

	/**
	 * Determines whether the items can be added at the specified index. The
	 * slot must already contain items equal to {@code item} parameter or be
	 * empty. Neither total mass nor volume of the added items may exceed the
	 * available mass and volume of the container. Additional restrictions may
	 * apply.
	 * <p>
	 * When index is valid, {@code item == null} and {@code count == 0}, this
	 * method returns {@code true}. Otherwise, when {@code count <= 0}, this
	 * method returns {@code false}.
	 * 
	 * @param index the index of the slot to query
	 * @param item  the item type
	 * @param count the amount of items to add
	 * @return {@code true} iff the operation is possible
	 */
	public boolean canAdd(int index, ItemData item, int count) {
		if (index < 0 || index >= maxPossibleSize) {
			return false;
		}

		if (item == null && count == 0) {
			return true;
		}

		if (count < 0) {
			return false;
		}

		ItemData currentItem = getItem(index);
		if (currentItem == null) {
			// Pass
		} else if (currentItem.equals(item)) {
			// Pass
		} else {
			return false;
		}

		if (!canAdd(item)) {
			return false;
		}

		float addedMass = item.getMass() * count;
		if (getMass() + addedMass > getMassLimit()) {
			return false;
		}

		float addedVolume = item.getVolume() * count;
		if (getVolume() + addedVolume > getVolumeLimit()) {
			return false;
		}

		return true;
	}

	/**
	 * Determines whether the items can be removed from the specified index. The
	 * slot must already contain items equal to {@code item} parameter. The item
	 * count must be no lower than {@code count} parameter. Additional
	 * restrictions may apply.
	 * <p>
	 * When index is valid, {@code item == null} and {@code count == 0}, this
	 * method returns {@code true}. Otherwise, when {@code count <= 0}, this
	 * method returns {@code false}.
	 * 
	 * @param index the index of the slot to query
	 * @param item  the item type
	 * @param count the amount of items to remove
	 * @return {@code true} iff the operation is possible
	 */
	public boolean canRemove(int index, ItemData item, int count) {
		if (index < 0 || index >= maxPossibleSize) {
			return false;
		}

		if (item == null && count == 0) {
			return true;
		}

		if (count < 0) {
			return false;
		}

		if (!isRemovingAllowed()) {
			return false;
		}

		ItemData currentItem = getItem(index);
		if (currentItem == null) {
			return false;
		} else if (currentItem.equals(item)) {
			// Pass
		} else {
			return false;
		}

		if (getCount(index) < count) {
			return false;
		}

		return true;
	}

	/**
	 * Attempts to add the provided items to the specified slot in the
	 * container. This method modifies the data structure directly; use an
	 * appropriate {@link Items} method to add items in a safe and convenient
	 * way.
	 * <p>
	 * A {@link #canAdd(int, ItemData, int)} check is performed. If the check
	 * fails, the method does not alter the contents of the container and
	 * returns {@code false}. If the check succeeds, the item type and item
	 * count of the referenced slot are altered appropriately and {@code true}
	 * is returned.
	 * <p>
	 * When {@code item == null} or {@code count <= 0}, this method returns
	 * {@code false}.
	 * 
	 * @param index the index of the slot to alter
	 * @param item  the item type
	 * @param count the amount of items to add
	 * @return {@code true} iff the container was changed as the result of this
	 *         operation
	 */
	protected abstract boolean add(int index, ItemData item, int count);

	/**
	 * Attempts to remove the provided items from the specified slot in the
	 * container. This method modifies the data structure directly; use an
	 * appropriate {@link Items} method to remove items in a safe and convenient
	 * way.
	 * <p>
	 * A {@link #canRemove(int, ItemData, int)} check is performed. If the check
	 * fails, the method does not alter the contents of the container and
	 * returns {@code false}. If the check succeeds, the item type and item
	 * count of the referenced slot are altered appropriately and {@code true}
	 * is returned.
	 * <p>
	 * When {@code item == null} or {@code count <= 0}, this method returns
	 * {@code false}.
	 * 
	 * @param index the index of the slot to alter
	 * @param item  the item type
	 * @param count the amount of items to remove
	 * @return {@code true} iff the container was changed as the result of this
	 *         operation
	 */
	protected abstract boolean remove(int index, ItemData item, int count);

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
		for (int i = 0; i < getMaxIndex(); ++i) {
			ItemData data = getItem(i);

			if (data == null) {
				continue;
			}

			sum += data.getMass() * getCount(i);
		}
		return sum;
	}

	public synchronized float getVolume() {
		float sum = 0;
		for (int i = 0; i < getMaxIndex(); ++i) {
			ItemData data = getItem(i);

			if (data == null) {
				continue;
			}

			sum += data.getVolume() * getCount(i);
		}
		return sum;
	}

	protected void fireSlotChangeEvent(int index) {
		if (getItem(index) instanceof ItemDataWithContainers) {
			subContainersCache.add(index);
		} else {
			subContainersCache.remove(index);
		}
		
		Inventory inventory = this.inventory;
		if (inventory == null || inventory.getEventBus() == null) {
			return;
		}

		inventory.getEventBus().post(new ItemSlotChangedEvent(this, index));
	}

	/**
	 * Checks class invariants and throws an {@link IllegalStateException} in
	 * case of discrepancies.
	 */
	protected synchronized void checkState() {
		int maxIndex = getMaxIndex();
		for (int index = 0; index < maxIndex; ++index) {

			ItemData item = getItem(index);
			int count = getCount(index);

			if ((item == null) != (count == 0)) {
				if (item == null) {
					throw new IllegalStateException("Item is null but count (" + count + ") != 0 in slot " + index);
				} else {
					throw new IllegalStateException("Item is " + item + " but count is zero in slot " + index);
				}
			}

			if (count < 0) {
				throw new IllegalStateException("count is negative: " + count + " in slot " + index);
			}

			boolean isContainer = item instanceof ItemDataWithContainers;
			if (isContainer != subContainersCache.contains(index)) {
				if (!isContainer) {
					throw new IllegalStateException(
						"subContainersCache is invalid: item in slot " + index + " (" + item
							+ ") is cached as a container"
					);
				} else {
					throw new IllegalStateException(
						"subContainersCache is invalid: item in slot " + index + " (" + item
							+ ") is not cached as a container"
					);
				}
			}
			
			if (isContainer) {
				if (containsRecursively(item, this)) {
					throw new IllegalStateException("Recursion detected in slot " + index);
				}
			}

		}

		// Using negation in following checks to trigger errors if any value is
		// NaN
		// (since all comparisons return false if any operand is NaN)

		float mass = getMass();
		if (!(mass >= 0)) {
			throw new IllegalStateException("Mass is negative: " + mass);
		}

		float massLimit = getMassLimit();
		if (!(mass <= massLimit)) {
			throw new IllegalStateException("Mass is greater than mass limit: " + mass + " > " + massLimit);
		}

		float volume = getVolume();
		if (!(volume >= 0)) {
			throw new IllegalStateException("Volume is negative: " + volume);
		}

		float volumeLimit = getVolumeLimit();
		if (!(volume <= volumeLimit)) {
			throw new IllegalStateException("Volume is greater than volume limit: " + volume + " > " + volumeLimit);
		}
	}

	@FunctionalInterface
	public interface SlotConsumer {
		void accept(ItemData item, int count);
	}

	/**
	 * Invokes the provided action for each slot in this container. The action
	 * is run for empty slots, in which case call is invoked with parameters
	 * {@code (null, 0)} for each empty slot. The exact amount of invocations is
	 * determined by {@link #getMaxIndex()}.
	 * 
	 * @param action the action to run
	 * @see #forEachItem(SlotConsumer)
	 */
	public synchronized void forEachSlot(SlotConsumer action) {
		int maxIndex = getMaxIndex();
		for (int i = 0; i < maxIndex; ++i) {
			action.accept(getItem(i), getCount(i));
		}
	}

	/**
	 * Invokes the provided action for each non-empty slot in this container.
	 * 
	 * @param action the action to run
	 * @see #forEachSlot(SlotConsumer)
	 */
	public synchronized void forEachItem(SlotConsumer action) {
		int maxIndex = getMaxIndex();
		for (int i = 0; i < maxIndex; ++i) {

			int count = getCount(i);
			if (count != 0) {
				action.accept(getItem(i), count);
			}

		}
	}

	public boolean isEmpty(int index) {
		return getCount(index) == 0;
	}

}
