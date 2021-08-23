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

import org.lwjgl.openal.*;
import ru.windcorp.progressia.client.audio.backend.AudioReader;
import ru.windcorp.progressia.client.audio.backend.Listener;
import ru.windcorp.progressia.client.audio.backend.SoundType;
import ru.windcorp.progressia.client.audio.backend.Speaker;
import ru.windcorp.progressia.common.resource.Resource;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC10.*;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {
	private static long device;
	private static ALCCapabilities deviceCapabilities;
	private static ALCapabilities alCapabilities;

	private static final int SOUNDS_NUM = 64;
	private static int lastSoundIndex = 0;

	private static List<Speaker> soundSpeakers = new ArrayList<>(SOUNDS_NUM);
	private static Speaker musicSpeaker;

	public static void initAL() {
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);

		device = alcOpenDevice(defaultDeviceName);

		int[] attributes = new int[1];
		long context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);

		deviceCapabilities = ALC.createCapabilities(device);
		alCapabilities = AL.createCapabilities(deviceCapabilities);

		checkALError();
		createBuffers();
	}

	public static void update() {
		// Position of the listener
		Listener.getInstance().update();

	}

	private static Speaker getLastSpeaker() {
		Speaker speaker;
		do {
			lastSoundIndex++;
			if (lastSoundIndex >= SOUNDS_NUM) {
				lastSoundIndex = 0;
			}
			speaker = soundSpeakers.get(lastSoundIndex);
		} while (speaker.getState().equals(Speaker.State.PLAYING_LOOP));
		return speaker;
	}

	public static Speaker initSpeaker(SoundType st) {
		Speaker speaker = getLastSpeaker();
		try {
			st.initSpeaker(speaker);
		} catch (Exception ex) {
			throw new RuntimeException();
		}
		return speaker;
	}

	public static Speaker initMusicSpeaker(SoundType st) {
		try {
			st.initSpeaker(musicSpeaker);
		} catch (Exception ex) {
			throw new RuntimeException();
		}
		return musicSpeaker;
	}

	public static void checkALError() {
		int errorCode = alGetError();
		if (alGetError() != AL_NO_ERROR) {
			throw new RuntimeException(String.valueOf(errorCode));
		}
	}

	public static void loadSound(Resource resource, String id, AudioFormat format) {
		if (format == AudioFormat.MONO) {
			AudioRegistry.getInstance().register(AudioReader.readAsMono(resource, id));
		} else {
			AudioRegistry.getInstance().register(AudioReader.readAsStereo(resource, id));
		}
	}

	public static void closeAL() {
		for (Speaker s : soundSpeakers) {
			alDeleteBuffers(s.getAudioData());
			alDeleteBuffers(s.getSourceData());
		}
		alDeleteBuffers(musicSpeaker.getAudioData());
		alDeleteBuffers(musicSpeaker.getSourceData());

		alcCloseDevice(device);
	}

	public static ALCapabilities getALCapabilities() {
		return alCapabilities;
	}

	public static ALCCapabilities getDeviceCapabilities() {
		return deviceCapabilities;
	}

	public static void createBuffers() {
		for (int i = 0; i < SOUNDS_NUM; ++i) {
			soundSpeakers.add(new Speaker());
		}

		musicSpeaker = new Speaker();
	}

	public static String ALversion() {
		return alGetString(AL11.AL_VERSION);
	}

}
