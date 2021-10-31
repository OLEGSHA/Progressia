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

import ru.windcorp.progressia.common.world.item.inventory.ItemSlot;

public class Items {

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
		synchronized (from) {
			synchronized (into) {

				ItemData item = from.getContents();

				int originalAmount = from.getAmount();
				int transferAmount = Math.min(originalAmount, max);

				while (transferAmount > 0 && !into.canInsert(item, transferAmount)) {
					transferAmount--;
				}
				
				if (transferAmount == 0) {
					return 0;
				}

				if (!from.canRemove(transferAmount)) {
					return 0;
				}

				into.setContents(item, into.getAmount() + transferAmount);
				from.setAmount(originalAmount - transferAmount);

				return transferAmount;

			}
		}
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
		return pour(from, into, Integer.MAX_VALUE);
	}
	
	/**
	 * Attempts to swap the contents of the two slots.
	 * 
	 * @param a one of the slots
	 * @param b the other slot
	 * 
	 * @return whether the swap succeeded
	 */
	public static boolean swap(ItemSlot a, ItemSlot b) {
		synchronized (a) {
			synchronized (b) {

				ItemData aItem = a.getContents();
				int aAmount = a.getAmount();
				
				ItemData bItem = b.getContents();
				int bAmount = b.getAmount();
				
				a.clear();
				b.clear();
				
				if (a.canInsert(bItem, bAmount) && b.canInsert(aItem, aAmount)) {
					a.setContents(bItem, bAmount);
					b.setContents(aItem, aAmount);
					return true;
				} else {
					a.setContents(aItem, aAmount);
					b.setContents(bItem, bAmount);
					return false;
				}
				
			}
		}
	}

	private Items() {
	}

}
