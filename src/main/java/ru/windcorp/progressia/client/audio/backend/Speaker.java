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

package ru.windcorp.progressia.client.audio.backend;

import glm.vec._3.Vec3;
import static org.lwjgl.openal.AL11.*;

public class Speaker {

	public enum State {
		NOT_PLAYING, PLAYING, PLAYING_LOOP
	}

	// Buffers
	private int audioData;
	private int sourceData;

	// Characteristics
	private Vec3 position = new Vec3();
	private Vec3 velocity = new Vec3();
	private float pitch = 1.0f;
	private float gain = 1.0f;
	private State state = State.NOT_PLAYING;

	public Speaker() {
		sourceData = alGenSources();
	}

	public Speaker(int audioData) {
		this();
		setAudioData(audioData);
	}

	public Speaker(int audioData, Vec3 position, Vec3 velocity, float pitch, float gain) {
		setAudioData(audioData);
		setPosition(position);
		setVelocity(velocity);
		setPitch(pitch);
		setGain(gain);
	}

	public Speaker(Vec3 position, Vec3 velocity, float pitch, float gain) {
		setPosition(position);
		setVelocity(velocity);
		setPitch(pitch);
		setGain(gain);
	}

	public void play() {
		alSourcePlay(sourceData);
		state = State.PLAYING;
	}

	public void playLoop() {
		alSourcei(sourceData, AL_LOOPING, AL_TRUE);
		alSourcePlay(sourceData);
		state = State.PLAYING_LOOP;
	}

	public void stop() {
		alSourceStop(sourceData);
		if (state == State.PLAYING_LOOP) {
			alSourcei(sourceData, AL_LOOPING, AL_FALSE);
		}
		state = State.NOT_PLAYING;
	}

	public void pause() {
		alSourcePause(sourceData);
		state = State.NOT_PLAYING;
	}

	public boolean isPlaying() {
		final int speakerState = alGetSourcei(sourceData, AL_SOURCE_STATE);
		if (speakerState == AL_PLAYING) {
			return true;
		} else {
			state = State.NOT_PLAYING;
			return false;
		}
	}

	// GETTERS & SETTERS

	public int getAudioData() {
		return audioData;
	}

	public int getSourceData() {
		return sourceData;
	}

	public void setAudioData(int audioData) {
		stop();
		this.audioData = audioData;
		alSourcei(this.sourceData, AL_BUFFER, audioData);
	}

	public void setPosition(Vec3 position) {
		this.position = position;
		alSource3f(sourceData, AL_POSITION, position.x, position.y, position.z);
	}

	public Vec3 getPosition() {
		return position;
	}

	public void setVelocity(Vec3 velocity) {
		alSource3f(sourceData, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
		this.velocity = velocity;
	}

	public Vec3 getVelocity() {
		return velocity;
	}

	public void setPitch(float pitch) {
		alSourcef(sourceData, AL_PITCH, pitch);
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	public void setGain(float gain) {
		alSourcef(sourceData, AL_GAIN, gain);
		this.gain = gain;
	}

	public float getGain() {
		return gain;
	}

	public State getState() {
		return state;
	}

}
