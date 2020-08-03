/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.graphics;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.optica.client.graphics.input.FrameResizeEvent;

public class GUI {
	
	private static final List<Layer> LAYERS = new ArrayList<>();
	
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
	
	public static Object getEventSubscriber() {
		return new Object() {
			
			@Subscribe
			public void onFrameResized(FrameResizeEvent event) {
				GUI.invalidateEverything();
			}
			
		};
	}

}
