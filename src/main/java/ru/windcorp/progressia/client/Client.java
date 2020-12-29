package ru.windcorp.progressia.client;

import org.apache.logging.log4j.LogManager;

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
		this.world = new WorldRender(world);
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
		LogManager.getLogger().info("LocalPlayer entity changed from {} to {}", lastKnownEntity, entity);
		
		if (entity == null) {
			getCamera().setAnchor(null);
			return;
		}
		
		getCamera().setAnchor(new EntityAnchor(
				getWorld().getEntityRenderable(entity)
		));
	}

}
