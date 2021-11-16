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
import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.world.item.ItemData;
import ru.windcorp.progressia.common.world.item.ItemDataRegistry;

public abstract class ItemContainerMixed extends ItemContainer {

	public static final int MAX_SLOTS = 10000;
	
	private static final int GROWTH_STEP = 10;
	private static final int MINIMUM_CAPACITY = 10;
	
	private ItemData[] items = new ItemData[MINIMUM_CAPACITY];
	private int[] counts = new int[MINIMUM_CAPACITY];

	public ItemContainerMixed(String id) {
		super(id, MAX_SLOTS);
	}
	
	protected void setCapacity(int minimumCapacity) {
		if (minimumCapacity < 0) {
			return;
		}
		
		int newCapacity = ((minimumCapacity - MINIMUM_CAPACITY - 1) / GROWTH_STEP + 1) * GROWTH_STEP + MINIMUM_CAPACITY;
		
		ItemData[] newItems = new ItemData[newCapacity];
		int[] newCounts = new int[newCapacity];
		
		int length = Math.min(this.items.length, newItems.length);
		System.arraycopy(this.items, 0, newItems, 0, length);
		System.arraycopy(this.counts, 0, newCounts, 0, length);
		
		this.items = newItems;
		this.counts = newCounts;
	}
	
	protected void ensureCapacity(int minimumCapacity) {
		if (items.length >= minimumCapacity) {
			return;
		}
		
		setCapacity(minimumCapacity);
	}

	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		int size = input.readInt();
		
		ensureCapacity(size);

		for (int index = 0; index < size; ++index) {

			ItemData item;
			int count = input.readInt();

			if (count != 0) {
				String id = input.readUTF();
				item = ItemDataRegistry.getInstance().create(id);
				item.read(input, context);
			} else {
				item = null;
			}

			items[index] = item;
			counts[index] = count;
			
			fireSlotChangeEvent(index);

		}

		checkState();
	}

	@Override
	public synchronized void write(DataOutput output, IOContext context) throws IOException {

		int size = items.length;
		output.writeInt(size);

		for (int index = 0; index < size; ++index) {
			output.writeInt(counts[index]);
			ItemData item = items[index];

			if (item != null) {
				output.writeUTF(item.getId());
				item.write(output, context);
			}
		}

	}

	@Override
	public void copy(Encodable destination) {
		ItemContainerMixed other = (ItemContainerMixed) destination;
		int myLength = this.items.length;

		synchronized (this) {
			synchronized (other) {
				
				other.setCapacity(myLength);
				System.arraycopy(this.counts, 0, other.counts, 0, myLength);

				for (int i = 0; i < myLength; ++i) {
					ItemData myItem = this.items[i];
					ItemData otherItem;

					if (myItem == null) {
						otherItem = null;
					} else {
						otherItem = ItemDataRegistry.getInstance().create(myItem.getId());
						myItem.copy(otherItem);
					}

					other.items[i] = otherItem;
					other.fireSlotChangeEvent(i);
				}

			}
		}
	}

	@Override
	public ItemData getItem(int index) {
		if (index < 0 || index >= items.length) {
			return null;
		}
		return items[index];
	}

	@Override
	public int getCount(int index) {
		if (index < 0 || index >= counts.length) {
			return 0;
		}
		return counts[index];
	}

	@Override
	public int getMaxIndex() {
		return items.length;
	}

	@Override
	protected boolean add(int index, ItemData item, int count) {
		if (!canAdd(index, item, count)) {
			return false;
		}
		
		if (item != null) {
			ensureCapacity(index + 1);
			this.items[index] = item;
			this.counts[index] += count;
			fireSlotChangeEvent(index);
			checkState();
		}
		
		return true;
	}

	@Override
	protected boolean remove(int index, ItemData item, int count) {
		if (!canRemove(index, item, count)) {
			return false;
		}

		if (count != 0) {
			this.counts[index] -= count;
			
			if (this.counts[index] == 0) {
				this.items[index] = null;
				shrinkIfPossible();
			}
	
			fireSlotChangeEvent(index);
			checkState();
		}
		
		return true;
	}

	protected void shrinkIfPossible() {
		int upperBound;
		
		for (upperBound = counts.length; upperBound > MINIMUM_CAPACITY; --upperBound) {
			if (counts[upperBound - 1] != 0) {
				break;
			}
		}
		
		if (upperBound != counts.length) {
			setCapacity(upperBound);
		}
	}

	@Override
	protected synchronized void checkState() {
		super.checkState();
		
		if (items.length > MAX_SLOTS) {
			throw new IllegalStateException("Container has more than " + MAX_SLOTS + " slots (items): " + items.length);
		}

		if (counts.length > MAX_SLOTS) {
			throw new IllegalStateException(
				"Container has more than " + MAX_SLOTS + " slots (counts): " + counts.length
			);
		}
	}

}
