package ru.windcorp.progressia.client.audio.backend;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.resource.ResourceManager;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

public class ALTest {

	// Buffers hold sound data
	private IntBuffer buffer = BufferUtils.createIntBuffer(1);
	// Sources are points emitting sound
	private IntBuffer source = BufferUtils.createIntBuffer(1);
	// Position of the source sound
	private FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
	// Velocity of the source sound
	private FloatBuffer sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
	// Position of the listener
	private FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
	// Velocity of the listener
	private FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();

	// Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	private FloatBuffer listenerOri =
			(FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).flip();

	private ShortBuffer rawDataBuffer;

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
		checkALError();
		int thebuffer = AL10.alGenBuffers();
		checkALError();

		IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer sampleRateBuffer = BufferUtils.createIntBuffer(1);


		ShortBuffer rawAudioBuffer =
				readVorbis("assets/sounds/sample.ogg", channelsBuffer, sampleRateBuffer);
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();

		int format = -1;
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}

		System.out.println(rawAudioBuffer);
		//Send the data to OpenAL
		alBufferData(thebuffer, AL_FORMAT_STEREO16, rawAudioBuffer, sampleRate);
		//Free the memory allocated by STB
		//free(rawAudioBuffer);

		alGenSources(source);

		//Assign our buffer to the source
		alSourcei(source.get(0), AL_BUFFER, thebuffer);

		// Bind the buffer with the source
		//AL10.alGenBuffers(source);
		checkALError();

		/*alSourcei(source.get(0),	 AL_BUFFER,		buffer.get(0)	);
		alSourcef(source.get(0),	 AL_PITCH,		1.0f		);
		alSourcef(source.get(0),	 AL_GAIN,		1.0f		);
		alSourcefv(source.get(0),	 AL_POSITION,	sourcePos		);
		alSourcefv(source.get(0),	 AL_VELOCITY, 	sourceVel		);*/

		int error = alGetError();

		checkALError();
	}

	//Tells OpenAL to use the data we have created
	void setListenerValues() {
		alListenerfv(AL_POSITION,		listenerPos);
		alListenerfv(AL_VELOCITY,		listenerVel);
		alListenerfv(AL_ORIENTATION,	listenerOri);
	}

	void killALData() {
		//alDeleteSources(source);
		//alDeleteBuffers(buffer);
	}

	public void execute() {
		initializeAL();

		checkALError();

		loadALData();


		setListenerValues();
		checkALError();
		AL10.alSourcePlay(source.get(0));
		checkALError();
	}

	private static ShortBuffer readVorbis(String fileName, IntBuffer channelsBuffer, IntBuffer sampleRateBuffer) {
		Resource res = ResourceManager.getResource(fileName);
		System.out.println();
		System.out.println(res.readAsBytes().remaining());
		System.out.println();
		return stb_vorbis_decode_memory(res.readAsBytes(), channelsBuffer, sampleRateBuffer);
	}

	public void checkALError() {
		int i = alGetError();
		if(alGetError() != AL_NO_ERROR) {
			throw new RuntimeException(i+"");
		}
	}
}