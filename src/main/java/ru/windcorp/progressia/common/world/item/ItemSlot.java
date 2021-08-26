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
 * found in {@link ItemContainer}s.
 */
public class ItemSlot implements Encodable {

	private ItemData contents;

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
	 * previously, it is discarded.
	 * 
	 * @param contents the new contents of this slot or {@code null} to clear
	 *                 the slot
	 */
	public synchronized final void setContents(ItemData contents) {
		this.contents = contents;
	}

	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		if (input.readBoolean()) {
			String id = input.readUTF();
			contents = ItemDataRegistry.getInstance().create(id);
			contents.read(input, context);
		} else {
			contents = null;
		}
	}

	@Override
	public synchronized void write(DataOutput output, IOContext context) throws IOException {
		output.writeBoolean(contents != null);
		if (contents != null) {
			output.writeUTF(contents.getId());
			contents.write(output, context);
		}
	}

	@Override
	public void copy(Encodable destination) {
		ItemSlot slot = (ItemSlot) destination;

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
