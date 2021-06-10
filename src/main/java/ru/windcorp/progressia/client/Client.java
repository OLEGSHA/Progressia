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

package ru.windcorp.progressia.client;

import ru.windcorp.progressia.client.comms.DefaultClientCommsListener;
import ru.windcorp.progressia.client.comms.ServerCommsChannel;
import ru.windcorp.progressia.client.graphics.world.Camera;
import ru.windcorp.progressia.client.graphics.world.EntityAnchor;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class Client {

	private final WorldRender world;
	private final LocalPlayer localPlayer = new LocalPlayer(this);

	private final Camera camera = new Camera((float) Math.toRadians(70));

	private final ServerCommsChannel comms;

	public Client(WorldData world, ServerCommsChannel comms) {
		this.world = new WorldRender(world, this);
		this.comms = comms;

		comms.addListener(new DefaultClientCommsListener(this));
	}

	public WorldRender getWorld() {
		return world;
	}

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public boolean isReady() {
		return localPlayer.hasEntity();
	}

	public Camera getCamera() {
		return camera;
	}

	public ServerCommsChannel getComms() {
		return comms;
	}

	public void onLocalPlayerEntityChanged(EntityData entity, EntityData lastKnownEntity) {
		if (entity == null) {
			getCamera().setAnchor(null);
			return;
		}

		getCamera().setAnchor(new EntityAnchor(getWorld().getEntityRenderable(entity)));
	}

}
