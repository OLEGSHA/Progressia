package ru.windcorp.progressia.client.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC10.*;

import java.util.concurrent.ArrayBlockingQueue;

public class SoundManager {
	
	private static final int SOURCES_NUM = 64;
	private static int lastSourceIndex = -1;
	private static final int[] SOURCES = new int[SOURCES_NUM];
	
	private static final ArrayBlockingQueue<Sound> SOUNDS =
		new ArrayBlockingQueue<>(SOURCES_NUM);
	
	private static long device;
	
	private static ALCCapabilities deviceCapabilities;
	private static ALCapabilities alCapabilities;
	
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
		
		alGenSources(SOURCES);
	}
	
	public static void update() {
		// Position of the listener
		Listener.getInstance().update();
	}
	
	private static void addSound(Sound sound) {
		if (!SOUNDS.offer(sound)) {
			Sound polled = SOUNDS.poll();
			assert polled != null;
			polled.stop();
			if (!SOUNDS.offer(sound)) {
				throw new RuntimeException();
			}
		}
	}
	
	private static int getNextSource() {
		if (++lastSourceIndex > SOURCES_NUM)
			lastSourceIndex = 0;
		return SOURCES[lastSourceIndex];
	}
	
	public static Sound createSound(SoundType soundType) {
		Sound sound = soundType.genSoundSource(getNextSource());
		addSound(sound);
		return sound;
	}
	
	public static void clearSounds() {
		Sound polled = SOUNDS.poll();
		while (polled != null) {
			polled.stop();
			polled = SOUNDS.poll();
		}
	}
	
	public static void checkALError() {
		int errorCode = alGetError();
		if (alGetError() != AL_NO_ERROR) {
			throw new RuntimeException(String.valueOf(errorCode));
		}
	}
	
	public static void closeAL() {
		clearSounds();
		alDeleteSources(SOURCES);
		for (Sound s : SOUNDS) {
			alDeleteBuffers(s.getAudio());
		}
		alcCloseDevice(device);
	}
	
	public static ALCapabilities getALCapabilities() {
		return alCapabilities;
	}
	
	public static ALCCapabilities getDeviceCapabilities() {
		return deviceCapabilities;
	}
	
}
