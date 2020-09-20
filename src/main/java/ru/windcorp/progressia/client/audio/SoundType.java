package ru.windcorp.progressia.client.audio;

import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;

public class SoundType {
    private ShortBuffer rawAudio;
    private int sampleRate;
    private int format;

    public SoundType(ShortBuffer rawAudio, int format, int sampleRate) {
        this.rawAudio = rawAudio;
        this.sampleRate = sampleRate;
        this.format = format;
    }

    public Sound genSound() {
        int audio = alGenBuffers();
        alBufferData(audio, format, rawAudio, sampleRate);
        return new Sound(audio, alGenSources());
    }
}
