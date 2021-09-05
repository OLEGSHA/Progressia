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
package ru.windcorp.progressia.test.inv;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.world.hud.SlotComponent;
import ru.windcorp.progressia.common.world.item.ItemContainer;
import ru.windcorp.progressia.common.world.item.ItemSlot;

public class DecoratedSlotComponent extends Button {
	
	private final SlotComponent slotComponent;

	public DecoratedSlotComponent(String name, ItemContainer container, int index) {
		this(name, new SlotComponent(name, container, index));
	}
	
	public DecoratedSlotComponent(String name, SlotComponent component) {
		super(name, null, null);
		this.slotComponent = component;
		
		Vec2i size = slotComponent.getPreferredSize().add(2 * BORDER);
		setPreferredSize(size);
		
		addChild(this.slotComponent);
		setLayout(new LayoutFill(MARGIN));
	}

	public ItemSlot getSlot() {
		return slotComponent.getSlot();
	}

}
