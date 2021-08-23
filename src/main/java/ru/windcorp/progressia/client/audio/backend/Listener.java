/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.client.audio.backend;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.world.Camera;

import static org.lwjgl.openal.AL10.*;

public class Listener {

	private static final Listener INSTANCE = new Listener();

	private Listener() {
	}

	public static Listener getInstance() {
		return INSTANCE;
	}

	// Params
	private final Vec3 position = new Vec3();
	private final Vec3 velocity = new Vec3();
	private final Vec3 oriAt = new Vec3();
	private final Vec3 oriUp = new Vec3();

	private boolean isInWorld = false;

	public void update() {
		Client client = ClientState.getInstance();
		Camera camera = client == null ? null : client.getCamera();

		boolean wasInWorld = isInWorld;
		isInWorld = client != null && camera.getAnchor() != null;

		if (isInWorld) {

			if (wasInWorld) {
				velocity.set(camera.getLastAnchorPosition()).sub(position)
						.div((float) GraphicsInterface.getFrameLength());
			} else {
				// If !wasInWorld, previous position is nonsence. Assume 0.
				velocity.set(0);
			}

			position.set(camera.getLastAnchorPosition());

			oriAt.set(camera.getLastAnchorLookingAt());
			oriUp.set(camera.getLastAnchorUp());
		} else if (wasInWorld) { // Do not reset if we weren't in world
			resetParams();
		}

		/*
		 * Only apply if there is a chance that params changed. This can only
		 * happen if we are in world now (isInWorld) or we just left world
		 * (wasInWorld, then we need to reset).
		 */
		if (isInWorld || wasInWorld) {
			applyParams();
		}
	}

	private void resetParams() {
		position.set(0);
		velocity.set(0);
		oriAt.set(0);
		oriUp.set(0);
	}

	private void applyParams() {
		alListener3f(AL_POSITION, position.x, position.y, position.z);
		alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
		alListenerfv(AL_ORIENTATION, new float[] { oriAt.x, oriAt.y, oriAt.z, oriUp.x, oriUp.y, oriUp.z });
	}

}
