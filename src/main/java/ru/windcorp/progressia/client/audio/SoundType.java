package ru.windcorp.progressia.client.audio;

import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL11.*;

public class SoundType {
    private ShortBuffer rawAudio;
    private int sampleRate;
    private int format;

    public SoundType(ShortBuffer rawAudio, int format, int sampleRate) {
        this.rawAudio = rawAudio;
        this.sampleRate = sampleRate;
        this.format = format;
    }

    public static int genEmptyAudio() {
        return alGenBuffers();
    }

    public static int genEmptySource() {
        return alGenSources();
    }

    public int genAudio() {
        int audio = alGenBuffers();
        alBufferData(audio, format, rawAudio, sampleRate);
        return audio;
    }

    public Sound genSoundSource() {
        return new Sound(genAudio(), alGenSources());
    }

    public Sound genSoundSource(int source) {
        if(!alIsSource(source)) throw new RuntimeException();
        return new Sound(genAudio(), source);
    }

    public Sound genSoundSource(int source, int audio) {
        if(!alIsBuffer(audio) || !alIsSource(source)) throw new RuntimeException();
        alBufferData(audio, format, rawAudio, sampleRate);
        return new Sound(audio, alGenSources());
    }
}
