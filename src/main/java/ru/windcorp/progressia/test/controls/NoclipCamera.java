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
package ru.windcorp.progressia.test.controls;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.world.Camera;
import ru.windcorp.progressia.client.graphics.world.EntityAnchor;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.client.world.entity.EntityRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;

public class NoclipCamera {

	private static class NoclipEntityRender extends EntityRender {

		public NoclipEntityRender() {
			super("Test:NoclipCamera");
		}
		
		@Override
		public EntityRenderable createRenderable(EntityData entity) {
			return new NoclipEntityRenderable(entity);
		}
		
	}
	
	private static class NoclipEntityRenderable extends EntityRenderable {

		public NoclipEntityRenderable(EntityData data) {
			super(data);
		}

		@Override
		protected void doRender(ShapeRenderHelper renderer) {
			// Do nothing
		}

	}
	
	public static void register() {
		EntityDataRegistry.getInstance().register("Test:NoclipCamera");
		EntityRenderRegistry.getInstance().register(new NoclipEntityRender());
	}
	
	public static void toggleNoclip() {
		if (ClientState.getInstance() == null || !ClientState.getInstance().isReady()) {
			return;
		}
		
		Camera camera = ClientState.getInstance().getCamera();
		WorldRender world = ClientState.getInstance().getWorld();
		EntityData player = ClientState.getInstance().getLocalPlayer().getEntity();
		
		List<EntityData> existingCameras = world.getData().getEntities().stream().filter(e -> e.getId().equals("Test:NoclipCamera")).collect(Collectors.toList());
		if (!existingCameras.isEmpty()) {
			existingCameras.forEach(world.getData()::removeEntity);
			camera.setAnchor(new EntityAnchor(world.getEntityRenderable(player)));
			return;
		}
		
		EntityData noclipCamera = EntityDataRegistry.getInstance().create("Test:NoclipCamera");
		
		noclipCamera.setLookingAt(player.getLookingAt());
		noclipCamera.setUpVector(player.getUpVector());
		noclipCamera.setPosition(player.getPosition());
		noclipCamera.setVelocity(player.getVelocity());
		noclipCamera.setEntityId(new Random().nextLong());
		
		player.setVelocity(new Vec3(0));
		
		world.getData().addEntity(noclipCamera);
		camera.setAnchor(new EntityAnchor(world.getEntityRenderable(noclipCamera)));
	}

}
