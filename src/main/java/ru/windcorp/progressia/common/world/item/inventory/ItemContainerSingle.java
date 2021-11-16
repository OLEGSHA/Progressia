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

public abstract class ItemContainerSingle extends ItemContainer {
	
	private ItemData item;
	private int count;
	private ItemSlot slot = new ItemSlot(this, 0);

	public ItemContainerSingle(String id) {
		super(id, 1);
	}

	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		count = input.readInt();
		if (count != 0) {
			String id = input.readUTF();
			item = ItemDataRegistry.getInstance().create(id);
			item.read(input, context);
		} else {
			item = null;
		}
		
		fireSlotChangeEvent(0);
		checkState();
	}

	@Override
	public synchronized void write(DataOutput output, IOContext context) throws IOException {
		output.writeInt(count);
		if (item != null) {
			output.writeUTF(item.getId());
			item.write(output, context);
		}
	}

	@Override
	public void copy(Encodable destination) {
		ItemContainerSingle other = (ItemContainerSingle) destination;

		synchronized (this) {
			synchronized (other) {
				other.count = this.count;
				
				if (this.item == null) {
					other.item = null;
				} else {
					if (other.item == null || !other.item.isLike(this.item)) {
						other.item = ItemDataRegistry.getInstance().create(this.item.getId());
					}
					this.item.copy(other.item);
					other.fireSlotChangeEvent(0);
				}
			}
		}
	}

	@Override
	public ItemData getItem(int index) {
		if (index != 0) {
			return null;
		}
		return item;
	}
	
	public ItemData getItem() {
		return item;
	}

	@Override
	public int getCount(int index) {
		if (index != 0) {
			return 0;
		}
		return count;
	}
	
	public int getCount() {
		return count;
	}
	
	public ItemSlot slot() {
		return slot;
	}

	@Override
	public int getMaxIndex() {
		return 1;
	}
	
	@Override
	protected boolean add(int index, ItemData item, int count) {
		if (!canAdd(index, item, count)) {
			return false;
		}
		
		if (item != null) {
			this.item = item;
			this.count += count;
			fireSlotChangeEvent(0);
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
			this.count -= count;
			if (this.count == 0) {
				this.item = null;
			}
			fireSlotChangeEvent(0);
			checkState();
		}
		
		return true;
	}

}
