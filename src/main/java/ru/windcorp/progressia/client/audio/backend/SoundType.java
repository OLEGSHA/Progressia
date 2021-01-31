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

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL11.*;

public class SoundType extends Namespaced {

	private ShortBuffer rawAudio;
	private int sampleRate;
	private int format;
	private int audioBuffer;
	private int timeLength;

	public SoundType(
		String id,
		ShortBuffer rawAudio,
		int format,
		int sampleRate
	) {
		super(id);
		this.rawAudio = rawAudio;
		this.sampleRate = sampleRate;
		this.format = format;
		createAudioBuffer();
	}

	private void createAudioBuffer() {
		this.audioBuffer = alGenBuffers();
		alBufferData(audioBuffer, format, rawAudio, sampleRate);
		timeLength = rawAudio.limit() / sampleRate;
	}

	public void initSpeaker(Speaker speaker) {
		speaker.setAudioData(audioBuffer);
	}
	
	public int getTimeLength() {
		return timeLength;
	}
}