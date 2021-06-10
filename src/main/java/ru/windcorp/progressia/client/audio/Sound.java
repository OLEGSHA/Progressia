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

public class Sound {

	protected Vec3 position = new Vec3(0f, 0f, 0f);
	protected Vec3 velocity = new Vec3(0f, 0f, 0f);
	protected float pitch = 1.0f;
	protected float gain = 1.0f;
	protected int timeLength = 0;

	protected SoundType soundType;

	public Sound(SoundType soundType) {
		this.soundType = soundType;
	}

	public Sound(String id) {
		this(AudioRegistry.getInstance().get(id));
	}

	public Sound(String id, int timeLength, Vec3 position, Vec3 velocity, float pitch, float gain) {
		this(id);
		this.position = position;
		this.velocity = velocity;
		this.pitch = pitch;
		this.gain = gain;
	}

	public Sound(SoundType soundType, int timeLength, Vec3 position, Vec3 velocity, float pitch, float gain) {
		this(soundType);
		this.position = position;
		this.velocity = velocity;
		this.pitch = pitch;
		this.gain = gain;
	}

	protected Speaker initSpeaker() {
		return AudioManager.initSpeaker(soundType);
	}

	public void play(boolean loop) {
		Speaker speaker = initSpeaker();
		speaker.setGain(gain);
		speaker.setPitch(pitch);
		speaker.setPosition(position);
		speaker.setVelocity(velocity);

		if (loop) {
			speaker.playLoop();
		} else {
			speaker.play();
		}
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setPosition(Vec3 position) {
		this.position = position;
	}

	public void setVelocity(Vec3 velocity) {
		this.velocity = velocity;
	}

	public Vec3 getPosition() {
		return position;
	}

	public float getGain() {
		return gain;
	}

	public Vec3 getVelocity() {
		return velocity;
	}

	public float getPitch() {
		return pitch;
	}

	public double getDuration() {
		return soundType.getDuration();
	}

}
