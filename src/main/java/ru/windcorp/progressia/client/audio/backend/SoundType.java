package ru.windcorp.progressia.client.audio.backend;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL11.*;

public class SoundType extends Namespaced {

	private ShortBuffer rawAudio;
	private int sampleRate;
	private int format;
	private int audioBuffer;

	public SoundType(String id, ShortBuffer rawAudio,
					 int format, int sampleRate) {
		super(id);
		this.rawAudio = rawAudio;
		this.sampleRate = sampleRate;
		this.format = format;
		createAudioBuffer();
	}

	private void createAudioBuffer() {
		this.audioBuffer = alGenBuffers();
		alBufferData(audioBuffer, format, rawAudio, sampleRate);
	}

	//TODO What is this (Eugene Smirnov)
	private Speaker createSound(int source, int audio) {
		if (!alIsBuffer(audio) || !alIsSource(source))
			throw new RuntimeException();

		alBufferData(audio, format, rawAudio, sampleRate);
		return new Speaker(audio);
	}

	public void initSpeaker(Speaker speaker) {
		speaker.setAudioData(audioBuffer);
	}
}
