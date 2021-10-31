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
import java.util.function.Consumer;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;

public abstract class ItemContainerSingle extends ItemContainer {
	
	private final ItemSlot slot = new ItemSlot();
	
	private final float massLimit;
	private final float volumeLimit;

	public ItemContainerSingle(String id, float massLimit, float volumeLimit) {
		super(id);
		this.massLimit = massLimit;
		this.volumeLimit = volumeLimit;
		
		slot.setContainer(this);
	}

	@Override
	public void read(DataInput input, IOContext context) throws IOException {
		slot.read(input, context);
	}

	@Override
	public void write(DataOutput output, IOContext context) throws IOException {
		slot.write(output, context);
	}

	@Override
	public void copy(Encodable destination) {
		slot.copy(((ItemContainerSingle) destination).slot);
	}

	@Override
	public ItemSlot getSlot(int index) {
		if (index == 0) {
			return slot;
		} else {
			return null;
		}
	}
	
	public ItemSlot slot() {
		return slot;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

	@Override
	public void forEach(Consumer<? super ItemSlot> action) {
		action.accept(slot);
	}

	@Override
	public float getMassLimit() {
		return massLimit;
	}

	@Override
	public float getVolumeLimit() {
		return volumeLimit;
	}

}
