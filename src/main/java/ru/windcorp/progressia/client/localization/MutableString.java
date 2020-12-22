package ru.windcorp.progressia.client.localization;

import java.lang.ref.WeakReference;
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

	protected final Collection<WeakReference<Listener>> listeners =
			Collections.synchronizedCollection(new LinkedList<>());

	protected void pokeListeners() {
		//TODO extract as weak bus listener class
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
			((MutableString) obj).addListener(this::update);
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
