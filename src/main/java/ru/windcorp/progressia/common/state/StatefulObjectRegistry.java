package ru.windcorp.progressia.common.state;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.windcorp.progressia.common.util.Namespaced;
import ru.windcorp.progressia.common.util.NamespacedRegistry;

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
		 * @return the created object
		 */
		T build();
	}
	
	protected static class Type<T> extends Namespaced {

		private final Factory<T> factory;
		
		private final AtomicBoolean isRegistered = new AtomicBoolean(false);
		
		public Type(String namespace, String name, Factory<T> factory) {
			super(namespace, name);
			this.factory = factory;
		}

		public T build() {
			return factory.build();
		}
		
		public AtomicBoolean getRegistrationFlag() {
			return isRegistered;
		}
		
	}
	
	private final NamespacedRegistry<Type<T>> registry =
			new NamespacedRegistry<Type<T>>() {
				@Override
				public void register(Type<T> element) {
					super.register(element);
					StatefulObjectRegistry.this.register(element);
				};
			};
	
	private final Map<String, StatefulObjectLayout> layouts =
			Collections.synchronizedMap(new WeakHashMap<>());
	
	public StatefulObjectLayout getLayout(String id) {
		StatefulObjectLayout layout = layouts.get(id);
		
		if (layout == null) {
			throw new IllegalArgumentException(
					"ID " + id + " has not been registered"
			);
		}
		
		return layout;
	}

	protected void register(Type<T> type) {
		if (!type.getRegistrationFlag().compareAndSet(false, true)) {
			throw new IllegalStateException(
					"ID " + type.getId() + " is already registered"
			);
		}
		
		InspectingStatefulObjectLayout inspector =
				new InspectingStatefulObjectLayout(type.getId());
		
		layouts.put(type.getId(), inspector);
		
		// During initialization inspector collects necessary data
		type.build();
		
		layouts.put(type.getId(), inspector.compile());
	}
	
	public T create(String id) {
		return registry.get(id).build();
	}
	
	public void register(String namespace, String name, Factory<T> factory) {
		registry.register(new Type<>(namespace, name, factory));
	}

}
