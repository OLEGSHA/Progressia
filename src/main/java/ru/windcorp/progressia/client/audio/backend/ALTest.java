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
	private FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
	// Velocity of the listener
	private FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();

	// Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	private FloatBuffer listenerOri =
			(FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).flip();

	private void initializeAL() {

		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		long device = alcOpenDevice(defaultDeviceName);
		int[] attributes = {0};
		long context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		ALCapabilities alcaps = AL.createCapabilities(deviceCaps);

	}

	void loadALData() {
		Sound music = AudioReader.readAsMono("assets/sounds/sample_mono.ogg").genSound();
		music.forcePlay();
	}

	void killALData() {
		//alDeleteSources(source);
		//alDeleteBuffers(buffer);
	}

	public void execute() {
		initializeAL();
		checkALError();
		Listener.update();
		loadALData();

		checkALError();
		checkALError();
	}

	public void checkALError() {
		int i = alGetError();
		if(alGetError() != AL_NO_ERROR) {
			throw new RuntimeException(i+"");
		}
	}
}