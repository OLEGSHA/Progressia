package ru.windcorp.progressia.client;

import ru.windcorp.progressia.client.comms.DefaultClientCommsListener;
import ru.windcorp.progressia.client.comms.ServerCommsChannel;
import ru.windcorp.progressia.client.graphics.world.Camera;
import ru.windcorp.progressia.client.graphics.world.LocalPlayer;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class Client {
	
	private final WorldRender world;
	private LocalPlayer localPlayer;
	
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
	
	public void setLocalPlayer(EntityData localPlayer) {
		this.localPlayer = new LocalPlayer(localPlayer);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public ServerCommsChannel getComms() {
		return comms;
	}

}
