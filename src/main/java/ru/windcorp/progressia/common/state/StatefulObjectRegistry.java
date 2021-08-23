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

package ru.windcorp.progressia.common.state;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

/**
 * Registry-like object for identification of various {@link StatefulObject}
 * types, such as blocks vs tiles. This object stores and manages various
 * {@linkplain StatefulObjectLayout layouts}.
 */
public class StatefulObjectRegistry<T extends StatefulObject> {

	@FunctionalInterface
	public static interface Factory<T> {
		/**
		 * Initializes a new, independent instance of the stateful object.
		 * 
		 * @return the created object
		 */
		T build();
	}

	protected static class Type<T> extends Namespaced {

		private final Factory<T> factory;

		private final AtomicBoolean isRegistered = new AtomicBoolean(false);

		public Type(String id, Factory<T> factory) {
			super(id);
			this.factory = factory;
		}

		public T build() {
			return factory.build();
		}

		public AtomicBoolean getRegistrationFlag() {
			return isRegistered;
		}

	}

	private final NamespacedInstanceRegistry<Type<T>> registry = new NamespacedInstanceRegistry<Type<T>>() {
		@Override
		public void register(Type<T> element) {
			super.register(element);
			StatefulObjectRegistry.this.register(element);
		};
	};

	private final Map<String, StatefulObjectLayout> layouts = Collections.synchronizedMap(new WeakHashMap<>());

	public StatefulObjectLayout getLayout(String id) {
		StatefulObjectLayout layout = layouts.get(id);

		if (layout == null) {
			throw new IllegalArgumentException("ID " + id + " has not been registered");
		}

		return layout;
	}

	protected void register(Type<T> type) {
		if (!type.getRegistrationFlag().compareAndSet(false, true)) {
			throw new IllegalStateException("ID " + type.getId() + " is already registered");
		}

		InspectingStatefulObjectLayout inspector = new InspectingStatefulObjectLayout(type.getId());

		layouts.put(type.getId(), inspector);

		// During initialization inspector collects necessary data
		type.build();

		layouts.put(type.getId(), inspector.compile());
	}

	public T create(String id) {
		return registry.get(id).build();
	}

	public void register(String id, Factory<T> factory) {
		registry.register(new Type<>(id, factory));
	}

}
