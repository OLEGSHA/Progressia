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

package ru.windcorp.progressia.client.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.graphics.input.CursorEvent;
import ru.windcorp.progressia.client.graphics.input.FrameResizeEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.WheelEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;

public class GUI {

	private static final List<Layer> LAYERS = Collections.synchronizedList(new ArrayList<>());
	private static final List<Layer> UNMODIFIABLE_LAYERS = Collections.unmodifiableList(LAYERS);

	@FunctionalInterface
	private interface LayerStackModification {
		void affect(List<Layer> layers);
	}

	private static final List<LayerStackModification> MODIFICATION_QUEUE = Collections
			.synchronizedList(new ArrayList<>());

	private static class ModifiableInput extends Input {
		@Override
		public void initialize(InputEvent event, Target target) {
			super.initialize(event, target);
		}
	}

	private static final ModifiableInput THE_INPUT = new ModifiableInput();

	private GUI() {
	}

	public static void addBottomLayer(Layer layer) {
		modify(layers -> layers.add(layer));
	}

	public static void addTopLayer(Layer layer) {
		modify(layers -> layers.add(0, layer));
	}

	public static void removeLayer(Layer layer) {
		modify(layers -> layers.remove(layer));
	}

	private static void modify(LayerStackModification mod) {
		MODIFICATION_QUEUE.add(mod);
	}

	public static List<Layer> getLayers() {
		return UNMODIFIABLE_LAYERS;
	}

	public static void render() {
		synchronized (LAYERS) {
			MODIFICATION_QUEUE.forEach(action -> action.affect(LAYERS));
			MODIFICATION_QUEUE.clear();

			for (int i = LAYERS.size() - 1; i >= 0; --i) {
				LAYERS.get(i).render();
			}
		}
	}

	public static void invalidateEverything() {
		LAYERS.forEach(Layer::invalidate);
	}

	private static void dispatchInputEvent(InputEvent event) {
		Input.Target target;

		if (event instanceof KeyEvent) {
			if (((KeyEvent) event).isMouse()) {
				target = Input.Target.HOVERED;
			} else {
				target = Input.Target.FOCUSED;
			}
		} else if (event instanceof CursorEvent) {
			target = Input.Target.HOVERED;
		} else if (event instanceof WheelEvent) {
			target = Input.Target.HOVERED;
		} else if (event instanceof FrameResizeEvent) {
			return;
		} else {
			target = Input.Target.ALL;
		}

		THE_INPUT.initialize(event, target);
		LAYERS.forEach(l -> l.handleInput(THE_INPUT));
	}

	public static Object getEventSubscriber() {
		return new Object() {

			@Subscribe
			public void onFrameResized(FrameResizeEvent event) {
				GUI.invalidateEverything();
			}

			@Subscribe
			public void onInput(InputEvent event) {
				dispatchInputEvent(event);
			}

		};
	}

}
