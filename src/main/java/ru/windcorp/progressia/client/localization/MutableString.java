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

package ru.windcorp.progressia.client.localization;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

public abstract class MutableString {
	public interface Listener {
		void onUpdate();
	}

	protected String data;

	protected final Collection<WeakReference<Listener>> listeners = Collections
			.synchronizedCollection(new LinkedList<>());

	private Collection<Listener> myListeners = null;

	protected void pokeListeners() {
		// TODO extract as weak bus listener class
		synchronized (listeners) {
			Iterator<WeakReference<Listener>> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				Listener listenerOrNull = iterator.next().get();
				if (listenerOrNull == null) {
					iterator.remove();
				} else {
					listenerOrNull.onUpdate();
				}
			}
		}
	}

	public MutableString addListener(Listener listener) {
		listeners.add(new WeakReference<>(listener));
		return this;
	}

	public MutableString removeListener(Listener listener) {
		listeners.removeIf(ref -> listener.equals(ref.get()));
		return this;
	}

	protected void listen(Object obj) {
		if (obj instanceof MutableString) {
			if (myListeners == null) {
				myListeners = new ArrayList<>();
			}

			Listener listener = this::update;
			myListeners.add(listener);
			((MutableString) obj).addListener(listener);
		}
	}

	public String get() {
		if (data == null) {
			data = compute();
		}
		return data;
	}

	@Override
	public String toString() {
		return get();
	}

	public MutableString apply(Function<String, String> f) {
		return new MutableStringFunc(this, f);
	}

	public void update() {
		data = compute();
		pokeListeners();
	}

	protected abstract String compute();

	public static MutableString formatted(Object format, Object... args) {
		return new MutableStringFormatter(format, args);
	}

	public MutableString format(Object... args) {
		return formatted(this, args);
	}

	public MutableString append(Object... objects) {
		return new MutableStringConcat(this, objects);
	}
}
