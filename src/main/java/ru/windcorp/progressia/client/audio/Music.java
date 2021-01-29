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
 
package ru.windcorp.progressia.client.audio;

import glm.vec._3.Vec3;

public class Music 
	extends Sound {
	
	public Music(String id) {
		super(id);
	}

	public Music(
		String id,
		Vec3 velocity,
		float pitch,
		float gain
	) {
		this(id);
		super.velocity = velocity;
		super.pitch = pitch;
		super.gain = gain;
	}
	
	@Override
	public void setPosition(Vec3 position) {
		throw new UnsupportedOperationException();
	}
}
