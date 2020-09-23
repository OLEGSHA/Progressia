package ru.windcorp.progressia.client.audio;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class SoundManager {
    private static IntBuffer sources = BufferUtils.createIntBuffer(65);
    private static ArrayBlockingQueue<Sound> sounds = new ArrayBlockingQueue<>(64);

    public static void update() {

    }

    public static void addSound(Sound sound) {
        if (!sounds.offer(sound)) {
            Sound polled = sounds.poll();
            assert polled != null;
            polled.flush();
            if (!sounds.offer(sound)) {
                throw new RuntimeException();
            }
        }
    }

    public static void clearSounds() {
        Sound polled = sounds.poll();
        while(polled != null) {
            polled.stop();
            polled = sounds.poll();
        }
    }
}
