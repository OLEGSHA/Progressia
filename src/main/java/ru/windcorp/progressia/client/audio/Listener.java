package ru.windcorp.progressia.client.audio;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.world.Camera;

import static org.lwjgl.openal.AL10.*;

//TODO add getters and setters

public class Listener {

    private static final Listener instance = new Listener();
    private Listener() {}
    public static Listener getInstance() {
        return instance;
    }

    //Params
    private final Vec3 position = new Vec3();
    private final Vec3 velocity  = new Vec3();
    private final Vec3 oriAt = new Vec3();
    private final Vec3 oriUp = new Vec3();

    private boolean isClientConnected = false;
    private Camera.Anchor anchor;

    public void update() {
        Client client = ClientState.getInstance();
        if (client == null) {
            if (isClientConnected) {
                isClientConnected = false;
                resetParams();
                applyParams();
            }
        } else {
            isClientConnected = true;
            if (anchor == null) {
                anchor = client.getCamera().getAnchor();
            } else {
                anchor.getCameraPosition(position);
                float pitch = anchor.getCameraPitch();
                float yaw = anchor.getCameraYaw();
                oriAt.set((float) (Math.cos(pitch) * Math.cos(yaw)),
                        (float) (Math.cos(pitch) * Math.sin(yaw)),
                        (float) Math.sin(pitch));
                oriUp.set((float) (Math.cos(pitch + Math.PI / 2) * Math.cos(yaw)),
                        (float) (Math.cos(pitch + Math.PI / 2) * Math.sin(yaw)),
                        (float) Math.sin(pitch + Math.PI / 2));
                applyParams();
            }
        }
    }

    private void resetParams() {
        position.set(0f, 0f, 0f);
        velocity.set(0f, 0f, 0f);
        oriAt.set(0f, 0f, 0f);
        oriUp.set(0f, 0f, 0f);
    }

    private void applyParams() {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
        alListenerfv(AL_ORIENTATION, new float[]{oriAt.x, oriAt.y, oriAt.z, oriUp.x, oriUp.y, oriUp.z});
    }
}