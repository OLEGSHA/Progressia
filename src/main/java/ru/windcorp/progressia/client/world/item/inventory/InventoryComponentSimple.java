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
package ru.windcorp.progressia.client.world.item.inventory;

import java.util.ArrayList;
import java.util.Collection;

import ru.windcorp.progressia.client.graphics.gui.layout.LayoutFill;
import ru.windcorp.progressia.client.graphics.world.hud.HUDWorkspace;
import ru.windcorp.progressia.common.world.item.inventory.Inventory;
import ru.windcorp.progressia.common.world.item.inventory.ItemContainer;

public class InventoryComponentSimple extends InventoryComponent {
	
	private final Inventory inventory;
	private final Collection<ContainerComponentSimple> containers = new ArrayList<>();

	public InventoryComponentSimple(String name, Inventory inventory, HUDWorkspace workspace) {
		super(name);
		setLayout(new LayoutFill());
		this.inventory = inventory;
		
		for (ItemContainer container : inventory.getContainers()) {
			ContainerComponentSimple component = new ContainerComponentSimple(container, workspace);
			containers.add(component);
			addChild(component);
		}
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Collection<? extends ContainerComponent> getContainers() {
		return containers;
	}

}
