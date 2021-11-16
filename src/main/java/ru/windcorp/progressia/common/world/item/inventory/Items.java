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
import ru.windcorp.progressia.common.world.item.LinearItemProperty;

public class Items {

	private static int pour(ItemContainer from, int fromIndex, ItemContainer into, int intoIndex, int max) {
		synchronized (from) {
			synchronized (into) {

				if (!from.isRemovingAllowed()) {
					return 0;
				}

				ItemData item = from.getItem(fromIndex);
				
				if (item == null) {
					return 0;
				}

				if (!into.canAdd(item)) {
					return 0;
				}

				if (!into.isEmpty(intoIndex) && !into.getItem(intoIndex).equals(item)) {
					return 0;
				}

				int originalCount = from.getCount(fromIndex);
				int transferCount = Math.min(originalCount, max);

				for (LinearItemProperty prop : LinearItemProperty.values()) {
					int canFitPropwise = (int) Math.floor((prop.getLimit(into) - prop.get(into)) / prop.get(item));
					if (canFitPropwise < transferCount) {
						transferCount = canFitPropwise;
					}
				}

				if (transferCount == 0) {
					return 0;
				}

				if (!into.canAdd(intoIndex, item, transferCount)) {
					return 0;
				}

				if (!from.remove(fromIndex, item, transferCount)) {
					return 0;
				}

				boolean success = into.add(intoIndex, item, transferCount);
				assert success : "Wait, canAdd and canRemove promised the operation would be safe!";

				return transferCount;

			}
		}
	}

	/**
	 * Attempts to transfer as many items as possible, but no more than
	 * {@code max}, between two slots. The origin of the items is {@code from},
	 * the destination is {@code into}. This method does nothing if the items
	 * could not be removed from origin or could not be inserted into
	 * destination, such as when {@code into} contains different items.
	 * 
	 * @param from the slot to transfer item from
	 * @param into the destination of the items
	 * @param max  the maximum amount of items to transfer. Use
	 *             {@code Integer.MAX_VALUE} to remove the limit
	 * @return the actual amount of items moved between slots
	 */
	public static int pour(ItemSlot from, ItemSlot into, int max) {
		return pour(from.getContainer(), from.getIndex(), into.getContainer(), into.getIndex(), max);
	}

	/**
	 * Attempts to transfer as many items as possible between two slots. The
	 * origin of the items is {@code from}, the destination is {@code into}.
	 * This method does nothing if the items could not be removed from origin or
	 * could not be inserted into destination, such as when {@code into}
	 * contains different items.
	 * 
	 * @param from the slot to transfer item from
	 * @param into the destination of the items
	 * @return the actual amount of items moved between slots
	 */
	public static int pour(ItemSlot from, ItemSlot into) {
		return pour(from.getContainer(), from.getIndex(), into.getContainer(), into.getIndex(), Integer.MAX_VALUE);
	}

	/**
	 * Attempts to swap the contents of the two slots. Swapping contents of two
	 * empty slots results in a no-op and is considered a success.
	 * 
	 * @param a one of the slots
	 * @param b the other slot
	 * @return whether the swap succeeded
	 */
	public static boolean swap(ItemSlot a, ItemSlot b) {
		synchronized (a.getContainer()) {
			synchronized (b.getContainer()) {

				if (a.isEmpty() && b.isEmpty()) {
					return true;
				}

				if (!a.isEmpty() && !a.getContainer().isRemovingAllowed()) {
					return false;
				}

				if (!b.isEmpty() && !b.getContainer().isRemovingAllowed()) {
					return false;
				}

				ItemData aItem = a.getItem();
				int aCount = a.getCount();

				ItemData bItem = b.getItem();
				int bCount = b.getCount();

				a.remove(aItem, aCount);
				b.remove(bItem, bCount);

				if (a.canAdd(bItem, bCount) && b.canAdd(aItem, aCount)) {
					a.add(bItem, bCount);
					b.add(aItem, aCount);
					return true;
				} else {
					a.add(aItem, aCount);
					b.add(bItem, bCount);
					return false;
				}

			}
		}
	}

	/**
	 * Attempts to place new items into the specified slot. Either all or none
	 * of the requested items will be spawned.
	 * 
	 * @param into  destination slot
	 * @param item  the item to add
	 * @param count the item count
	 * @return whether the addition succeeded
	 */
	public static boolean spawn(ItemSlot into, ItemData item, int count) {
		synchronized (into.getContainer()) {
			return into.add(item, count);
		}
	}

	/**
	 * Attempts to remove items from the specified slot. Either all or none of
	 * the requested items will be destroyed.
	 * 
	 * @param from  the slot
	 * @param item  the item to remove
	 * @param count the item count
	 * @return whether the removal succeeded
	 */
	public static boolean destroy(ItemSlot from, ItemData item, int count) {
		synchronized (from.getContainer()) {
			return from.remove(item, count);
		}
	}

	public static int pour(ItemSlot from, ItemContainer into, int max) {
		synchronized (from.getContainer()) {
			synchronized (into) {

				int totalPoured = 0;

				for (int index = 0; max > 0 && index <= into.getMaxIndex(); ++index) {
					int poured = pour(from.getContainer(), from.getIndex(), into, index, max);
					max -= poured;
					totalPoured += poured;
				}

				return totalPoured;

			}
		}
	}

	public static int pour(ItemSlot from, ItemContainer into) {
		return pour(from, into, Integer.MAX_VALUE);
	}

	public static boolean spawn(ItemContainer into, ItemData item, int count) {
		synchronized (into) {
			
			if (item == null && count == 0) {
				return true;
			}
			
			if (count < 0) {
				return false;
			}
			
			if (!into.canAdd(item)) {
				return false;
			}
			
			for (LinearItemProperty prop : LinearItemProperty.values()) {
				float requested = prop.get(item) * count;
				float available = prop.getLimit(into) - prop.get(into);
				if (requested > available) {
					return false;
				}
			}

			int compatibleSlot = -1;
			int firstEmptySlot = -1;
			
			for (int index = 0; index <= into.getMaxIndex(); ++index) {
				ItemData inSlot = into.getItem(index);
				if (inSlot == null) {
					if (firstEmptySlot == -1) {
						firstEmptySlot = index;
					}
				} else if (inSlot.equals(item)) {
					compatibleSlot = index;
					break;
				}
			}
			
			if (compatibleSlot == -1) {
				compatibleSlot = firstEmptySlot;
			}
			
			if (compatibleSlot == -1) {
				// Means the inventory is full due to slot limit
				return false;
			}
			
			return into.add(compatibleSlot, item, count);
			
		}
	}

	private Items() {
	}

}
