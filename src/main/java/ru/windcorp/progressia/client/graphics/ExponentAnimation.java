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

import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;

public class ExponentAnimation {
	
	private final float speed;
	private float value;
	
	public ExponentAnimation(float speed, float value) {
		this.speed = speed;
		this.value = value;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public float update(float target, double timeStep) {
		float difference = value - target;
		value += difference * (1 - Math.exp(speed * timeStep));
		
		float newDifference = value - target;
		if (difference * newDifference < 0) {
			// Whoops, we've overshot
			value = target;
		}
		
		return value;
	}
	
	public float updateForFrame(float target) {
		return update(target, GraphicsInterface.getFrameLength());
	}

}
