package ru.windcorp.progressia.client.audio;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;

//TODO add getters and setters

public class Listener {
    private static FloatBuffer position =
            (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
    private static FloatBuffer velocity  =
            (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip();
    private static FloatBuffer orientation =
            (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}).flip();

    public static void update() {
        alListenerfv(AL_POSITION,		position);
        alListenerfv(AL_VELOCITY,		velocity);
        alListenerfv(AL_ORIENTATION,	orientation);
    }
}
