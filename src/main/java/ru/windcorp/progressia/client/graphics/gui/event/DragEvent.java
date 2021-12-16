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
package ru.windcorp.progressia.client.graphics.gui.event;

import glm.vec._2.d.Vec2d;
import ru.windcorp.progressia.client.graphics.gui.Component;

public class DragEvent extends ComponentEvent {
	
	private final Vec2d currentChange = new Vec2d();
	private final Vec2d totalChange = new Vec2d();

	public DragEvent(Component component, Vec2d currentChange, Vec2d totalChange) {
		super(component);
		this.currentChange.set(currentChange.x, currentChange.y);
		this.totalChange.set(totalChange.x, totalChange.y);
	}
	
	public Vec2d getCurrentChange() {
		return currentChange;
	}
	
	public double getCurrentChangeX() {
		return currentChange.x;
	}
	
	public double getCurrentChangeY() {
		return currentChange.y;
	}
	
	public Vec2d getTotalChange() {
		return totalChange;
	}
	
	public double getTotalChangeX() {
		return totalChange.x;
	}
	
	public double getTotalChangeY() {
		return totalChange.y;
	}

}
