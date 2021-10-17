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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.eventbus.EventBus;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.crash.ReportingEventBus;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.item.ItemContainer;

public class Inventory extends Namespaced implements Encodable {
	
	private final ItemContainer[] containers;
	private final List<InventoryUser> users = new ArrayList<>();
	
	private EventBus eventBus = null;

	public Inventory(String id, ItemContainer... containers) {
		super(id);
		this.containers = containers;
		for (ItemContainer container : containers) {
			container.setInventory(this);
		}
	}
	
	public synchronized void open(InventoryUser user) {
		users.add(user);
		subscribe(user);
		eventBus.post(new InventoryOpenedEvent(this, user));
	}
	
	public synchronized void close(InventoryUser user) {
		if (eventBus != null) {
			eventBus.post(new InventoryClosingEvent(this, user));
		}
		
		users.remove(user);
		unsubscribe(user);
	}
	
	public synchronized void subscribe(Object listener) {
		if (eventBus == null) {
			eventBus = ReportingEventBus.create("Inventory " + getId());
		}
		eventBus.register(listener);
	}
	
	public synchronized void unsubscribe(Object listener) {
		if (eventBus == null) {
			return;
		}
		eventBus.unregister(listener);
	}
	
	public synchronized boolean isUser(InventoryUser user) {
		return users.contains(user);
	}
	
	public synchronized void forEachUser(Consumer<? super InventoryUser> action) {
		users.forEach(action);
	}
	
	public Collection<InventoryUser> getUsers() {
		return users;
	}
	
	public synchronized void closeAll() {
		while (!users.isEmpty()) {
			close(users.get(0));
		}
	}
	
	public ItemContainer[] getContainers() {
		return containers;
	}
	
	public synchronized float getMass() {
		float sum = 0;
		for (ItemContainer container : containers) {
			sum += container.getMass();
		}
		return sum;
	}
	
	public synchronized float getVolume() {
		float sum = 0;
		for (ItemContainer container : containers) {
			sum += container.getVolume();
		}
		return sum;
	}
	
	@Override
	public synchronized void read(DataInput input, IOContext context) throws IOException {
		for (ItemContainer container : containers) {
			container.read(input, context);
		}
	}
	
	@Override
	public synchronized void write(DataOutput output, IOContext context) throws IOException {
		for (ItemContainer container : containers) {
			container.write(output, context);
		}
	}
	
	@Override
	public synchronized void copy(Encodable destination) {
		Inventory inventory = (Inventory) destination;
		assert inventory.containers.length == containers.length;
		for (int i = 0; i < containers.length; ++i) {
			containers[i].copy(inventory.containers[i]);
		}
	}

}
