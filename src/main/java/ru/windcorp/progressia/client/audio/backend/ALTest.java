package ru.windcorp.progressia.client.audio.backend;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import ru.windcorp.progressia.client.audio.AudioReader;
import ru.windcorp.progressia.client.audio.Listener;
import ru.windcorp.progressia.client.audio.Sound;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class ALTest {
	// Position of the listener
	private static FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
	// Velocity of the listener
	private static FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();

	// Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	private static FloatBuffer listenerOri =
			(FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).flip();

	static private void initializeAL() {

		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		long device = alcOpenDevice(defaultDeviceName);
		int[] attributes = {0};
		long context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		ALCapabilities alcaps = AL.createCapabilities(deviceCaps);
	}

	static void loadALData() {
		alListenerfv(AL_POSITION,		listenerPos);
		alListenerfv(AL_VELOCITY,		listenerVel);
		alListenerfv(AL_ORIENTATION,	listenerOri);
		Sound music = AudioReader.readAsMono("assets/sounds/sample_mono.ogg").genSound();

		music.playOnce();
		checkALError();
	}

	static void killALData() {
		//alDeleteSources(source);
		//alDeleteBuffers(buffer);
	}

	public static void execute() {
		initializeAL();
		checkALError();
		checkALError();
		checkALError();
		loadALData();

		checkALError();
		checkALError();
	}

	public static void update() {
		Listener.getInstance().update();
	}

	public static void checkALError() {
		int i = alGetError();
		if(alGetError() != AL_NO_ERROR) {
			throw new RuntimeException(i+"");
		}
	}
}