/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.graphics.input.CursorEvent;
import ru.windcorp.progressia.client.graphics.input.FrameResizeEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.WheelEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;

public class GUI {
	
	private static final List<Layer> LAYERS = new ArrayList<>();
	
	private static class ModifiableInput extends Input {
		@Override
		public void initialize(InputEvent event, Target target) {
			super.initialize(event, target);
		}
	}
	
	private static final ModifiableInput THE_INPUT = new ModifiableInput();
	
	private GUI() {}
	
	public synchronized static void addBottomLayer(Layer layer) {
		LAYERS.add(layer);
	}
	
	public synchronized static void addTopLayer(Layer layer) {
		LAYERS.add(0, layer);
	}
	
	public synchronized static void removeLayer(Layer layer) {
		LAYERS.remove(layer);
	}
	
	public synchronized static void render() {
		for (int i = LAYERS.size() - 1; i >= 0; --i) {
			LAYERS.get(i).render();
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
