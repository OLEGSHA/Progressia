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

package ru.windcorp.progressia.client.graphics.input.bus;

import java.util.ArrayList;
import java.util.Collection;

import ru.windcorp.progressia.client.graphics.input.InputEvent;

public class InputBus {

	private static class WrappedListener {

		private final Class<?> type;
		private final boolean handleConsumed;
		private final InputListener<?> listener;

		public WrappedListener(Class<?> type, boolean handleConsumed, InputListener<?> listener) {
			this.type = type;
			this.handleConsumed = handleConsumed;
			this.listener = listener;
		}

		private boolean handles(Input input) {
			return (!input.isConsumed() || handleConsumed) && type.isInstance(input.getEvent());
		}

		@SuppressWarnings("unchecked")
		public void handle(Input input) {
			if (handles(input)) {
				boolean consumed = ((InputListener<InputEvent>) listener)
						.handle((InputEvent) type.cast(input.getEvent()));

				input.setConsumed(consumed);
			}
		}

	}

	private final Collection<WrappedListener> listeners = new ArrayList<>(4);

	public void dispatch(Input input) {
		listeners.forEach(l -> l.handle(input));
	}

	public <T extends InputEvent> void register(Class<? extends T> type, boolean handlesConsumed,
			InputListener<T> listener) {
		listeners.add(new WrappedListener(type, handlesConsumed, listener));
	}

	public <T extends InputEvent> void register(Class<? extends T> type, InputListener<T> listener) {
		register(type, false, listener);
	}

	public void unregister(InputListener<?> listener) {
		listeners.removeIf(l -> l.listener == listener);
	}

}
