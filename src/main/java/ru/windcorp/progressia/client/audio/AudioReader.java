package ru.windcorp.progressia.client.audio;

import org.lwjgl.BufferUtils;
import ru.windcorp.progressia.common.resource.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.openal.AL10.*;

public class AudioReader {
    private AudioReader() {};

    //TODO fix converting from mono-stereo

    public static SoundType readAsMono(String AudioFile) {
        IntBuffer channelBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer rateBuffer = BufferUtils.createIntBuffer(1);
        Resource res = ResourceManager.getResource(AudioFile);
        ShortBuffer rawMonoAudio = decodeVorbis(res, channelBuffer, rateBuffer);

        return new SoundType(rawMonoAudio, AL_FORMAT_MONO16, rateBuffer.get(0));
    }

    public static SoundType readAsStereo(String AudioFile) {
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(2);
        IntBuffer rateBuffer = BufferUtils.createIntBuffer(1);
        Resource res = ResourceManager.getResource(AudioFile);
        ShortBuffer rawStereoAudio = decodeVorbis(res, channelsBuffer, rateBuffer);

        return new SoundType(rawStereoAudio, AL_FORMAT_STEREO16, rateBuffer.get(0));
    }

    private static ShortBuffer decodeVorbis(Resource dataToDecode, IntBuffer channelsBuffer, IntBuffer rateBuffer) {
        return stb_vorbis_decode_memory(dataToDecode.readAsBytes(), channelsBuffer, rateBuffer);
    }
}
