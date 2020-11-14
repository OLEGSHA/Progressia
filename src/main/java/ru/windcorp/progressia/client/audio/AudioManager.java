package ru.windcorp.progressia.client.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import ru.windcorp.progressia.client.audio.backend.AudioReader;
import ru.windcorp.progressia.client.audio.backend.Listener;
import ru.windcorp.progressia.client.audio.backend.SoundType;
import ru.windcorp.progressia.client.audio.backend.Speaker;

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
	private static ArrayList<SoundType> soundsBuffer = new ArrayList<>();
	
	public static void initAL() {
		String defaultDeviceName = alcGetString(
				0,
				ALC_DEFAULT_DEVICE_SPECIFIER
		);

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
		} while (speaker.getState()
				.equals(Speaker.State.PLAYING_LOOP));
		return speaker;
	}

	private static SoundType findSoundType(String soundID) throws  Exception {
		for (SoundType s : soundsBuffer) {
			if (s.getId().equals(soundID)) {
				return s;
			}
		}
		throw new Exception("ERROR: The selected sound is not loaded or" +
							" not exists");
	}
	
	public static Speaker initSpeaker(String soundID) {
		Speaker speaker = getLastSpeaker();
		try {
			findSoundType(soundID).initSpeaker(speaker);
		} catch (Exception ex)
		{
			throw new RuntimeException();
		}
		return speaker;
	}

	public static Speaker initMusicSpeaker(String soundID) {
		try {
			findSoundType(soundID).initSpeaker(musicSpeaker);
		} catch (Exception ex)
		{
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

	public static void loadSound(String path, String namespace, String name,
								 AudioFormat format) {
		if (format == AudioFormat.MONO) {
			soundsBuffer.add(AudioReader.readAsMono(path, namespace, name));
		} else
		{
			soundsBuffer.add(AudioReader.readAsStereo(path, namespace, name));
		}
	}
	
	public static void closeAL() {
		//clearSounds();
		//TODO replace alDeleteSources(SOURCES);
		for (Speaker s : soundSpeakers) {
			alDeleteBuffers(s.getAudioData());
		}
		alcCloseDevice(device);
	}
	
	public static ALCapabilities getALCapabilities() {
		return alCapabilities;
	}
	
	public static ALCCapabilities getDeviceCapabilities() {
		return deviceCapabilities;
	}

	public static void createBuffers()
	{
		for (int i = 0; i < SOUNDS_NUM; ++i) {
			soundSpeakers.add(new Speaker());
		}

		musicSpeaker = new Speaker();
	}
	
}
