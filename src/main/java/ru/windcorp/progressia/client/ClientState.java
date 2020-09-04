package ru.windcorp.progressia.client;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.comms.localhost.LocalServerCommsChannel;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.flat.LayerTestUI;
import ru.windcorp.progressia.client.graphics.gui.LayerTestGUI;
import ru.windcorp.progressia.client.graphics.world.LayerWorld;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.ServerState;

public class ClientState {
	
	private static Client instance;
	
	public static Client getInstance() {
		return instance;
	}
	
	public static void setInstance(Client instance) {
		ClientState.instance = instance;
	}
	
	public static void connectToLocalServer() {
		
		WorldData world = new WorldData();
		Client client = new Client(world, new LocalServerCommsChannel(
				ServerState.getInstance()
		));
		
		client.setLocalPlayer(
				world.getChunk(new Vec3i(0, 0, 0)).getEntities().get(0)
		);
		
		setInstance(client);
		
		GUI.addBottomLayer(new LayerWorld(client));
		GUI.addTopLayer(new LayerTestUI());
		GUI.addTopLayer(new LayerTestGUI());
		
	}
	
	private ClientState() {}

}
