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

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;

/**
 * An entity optionally containing an {@link ItemData}. Item slots are typically
 * found in {@link ItemContainerMixed}s.
 */
public class ItemSlot implements Encodable {

	private ItemData contents;
	private int amount;

	/**
	 * Retrieves the contents of this slot.
	 * 
	 * @return the stored {@link ItemData} or {@code null}
	 */
	public synchronized final ItemData getContents() {
		return contents;
	}

	/**
	 * Sets the new contents of this slot. If an item stack was present
	 * previously, it is discarded. If the contents are {@code null}, the slot
	 * is emptied.
	 * <p>
	 * When the slot receives non-null contents, the new amount must be a
	 * positive integer. When the slot is emptied, {@code amount} must be 0.
	 * 
	 * @param contents the new contents of this slot or {@code null} to clear
	 *                 the slot
	 * @param amount   the amount of items to set.
	 *                 {@code (amount == 0) == (contents == null)} must be true.
	 */
	public synchronized final void setContents(ItemData contents, int amount) {
		this.contents = contents;
		this.amount = amount;
		
		checkState();
	}

	/**
	 * Sets the amount of items stored in this slot.
	 * <p>
	 * Setting the amount to zero also erases the slot's contents.
	 * 
	 * @param amount the new amount
	 */
	public synchronized void setAmount(int amount) {
		setContents(amount == 0 ? null : contents, amount);
	}
	
	/**
	 * Clears this slot
	 */
	public synchronized void clear() {
		setContents(null, 0);
	}

	/**
	 * Retrieves the amount of items stored in this slot. If not items are
	 * present, this returns 0.
	 * 
	 * @return the amount of items stored
	 */
	public synchronized int getAmount() {
		return amount;
	}
	
	public synchronized boolean isEmpty() {
		return amount == 0;
	}
	
	public synchronized boolean canInsert(ItemData contents, int amount) {
		
		// Ignore amount
		
		if (this.contents == null) {
			return true;
		}
		
		return this.contents.equals(contents);
	}
	
	public synchronized boolean canRemove(int amount) {
		return this.amount >= amount;
	}
	
	private synchronized void checkState() {
		if ((contents == null) != (amount == 0)) {
			if (contents == null) {
				throw new IllegalArgumentException("Contents is null but amount (" + amount + ") != 0");
			} else {
				throw new IllegalArgumentException("Contents is " + contents + " but amount is zero");
			}
		}
		
		if (amount < 0) {
			throw new IllegalArgumentException("amount is negative: " + amount);
		}
	}

	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		amount = input.readInt();
		if (amount != 0) {
			String id = input.readUTF();
			contents = ItemDataRegistry.getInstance().create(id);
			contents.read(input, context);
		} else {
			contents = null;
		}
		
		checkState();
	}

	@Override
	public synchronized void write(DataOutput output, IOContext context) throws IOException {
		output.writeInt(amount);
		if (contents != null) {
			output.writeUTF(contents.getId());
			contents.write(output, context);
		}
	}

	@Override
	public void copy(Encodable destination) {
		ItemSlot slot = (ItemSlot) destination;

		slot.amount = this.amount;
		
		if (this.contents == null) {
			slot.contents = null;
		} else {
			if (slot.contents == null || !slot.contents.isLike(this.contents)) {
				slot.contents = ItemDataRegistry.getInstance().create(this.contents.getId());
			}
			this.contents.copy(slot.contents);
		}
	}

}
