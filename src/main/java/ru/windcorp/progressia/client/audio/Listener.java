package ru.windcorp.progressia.client.audio;

import com.sun.jna.platform.unix.X11;
import glm.vec._3.Vec3;
import jglm.Vec;
import org.lwjgl.BufferUtils;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.world.Camera;
import ru.windcorp.progressia.common.util.FloatMathUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;

//TODO add getters and setters

public class Listener {
    private static Vec3 position = new Vec3();
    private static Vec3 velocity  = new Vec3();
    private static Vec3 oriAt = new Vec3();
    private static Vec3 oriUp = new Vec3();

    private static Client client;
    private static Camera.Anchor anchor;

    public static void update() {
        if (client == null) {
            client = ClientState.getInstance();
        } else if (anchor == null) {
            anchor = client.getCamera().getAnchor();
        } else {
            anchor.getCameraPosition(position);
            float pitch = anchor.getCameraPitch();
            float yaw = anchor.getCameraYaw();
            oriAt.set(  (float) (Math.cos(pitch) * Math.cos(yaw)),
                        (float) (Math.cos(pitch) * Math.sin(yaw)),
                        (float) Math.sin(pitch)                 );
            oriUp.set(  (float) (Math.cos(pitch + Math.PI / 2) * Math.cos(yaw)),
                        (float) (Math.cos(pitch + Math.PI / 2) * Math.sin(yaw)),
                        (float) Math.sin(pitch + Math.PI / 2)                 );

            alListener3f(AL_POSITION, position.x, position.y, position.z);
            alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
            alListenerfv(AL_ORIENTATION,	new float[] {oriAt.x, oriAt.y, oriAt.z, oriUp.x, oriUp.y, oriUp.z});
        }
    }
}