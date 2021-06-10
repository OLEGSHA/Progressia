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
import ru.windcorp.progressia.client.audio.backend.SoundType;
import ru.windcorp.progressia.client.audio.backend.Speaker;

public class Music extends Sound {

	public Music(SoundType soundType, int timeLength, float pitch, float gain) {
		super(soundType, timeLength, new Vec3(), new Vec3(), pitch, gain);
	}

	public Music(SoundType soundType) {
		super(soundType);
	}

	public Music(String id, int timeLength, float pitch, float gain) {
		super(id, timeLength, new Vec3(), new Vec3(), pitch, gain);
	}

	public Music(String id) {
		super(id);
	}

	@Override
	protected Speaker initSpeaker() {
		return AudioManager.initMusicSpeaker(soundType);
	}

	@Override
	public void setPosition(Vec3 position) {
		throw new UnsupportedOperationException();
	}
}
