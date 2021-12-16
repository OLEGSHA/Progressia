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
package ru.windcorp.progressia.client.graphics.gui;

import java.util.Objects;

import glm.vec._2.d.Vec2d;
import ru.windcorp.progressia.client.graphics.gui.event.DragEvent;
import ru.windcorp.progressia.client.graphics.gui.event.DragStartEvent;
import ru.windcorp.progressia.client.graphics.gui.event.DragStopEvent;
import ru.windcorp.progressia.client.graphics.input.CursorMoveEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.input.bus.InputBus;

public class DragManager {
	
	private Component component;
	
	private boolean isDragged = false;
	private final Vec2d change = new Vec2d();
	
	public void install(Component c) {
		Objects.requireNonNull(c, "c");
		if (c == component) {
			return;
		}
		if (component != null) {
			throw new IllegalStateException("Already installed on " + component + "; attempted to install on " + c);
		}
		
		component = c;
		
		c.addInputListener(CursorMoveEvent.class, this::onCursorMove, InputBus.Option.ALWAYS);
		c.addKeyListener(KeyMatcher.LMB, this::onLMB, InputBus.Option.ALWAYS, InputBus.Option.IGNORE_ACTION);
	}
	
	private void onCursorMove(CursorMoveEvent e) {
		if (isDragged) {
			Vec2d currentChange = e.getChange(null);
			change.add(currentChange);
			component.dispatchEvent(new DragEvent(component, currentChange, change));
		}
	}
	
	private void onLMB(KeyEvent e) {
		if (isDragged && e.isRelease()) {
			
			isDragged = false;
			component.dispatchEvent(new DragStopEvent(component, change));
			
		} else if (!isDragged && !e.isConsumed() && e.isPress() && component.isHovered()) {
			
			isDragged = true;
			change.set(0, 0);
			component.dispatchEvent(new DragStartEvent(component));
			e.consume();
			
		}
	}

}
