package ru.windcorp.progressia.test;

import java.io.IOException;

import glm.Glm;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.PacketLoadChunk;
import ru.windcorp.progressia.common.world.PacketSetLocalPlayer;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Server;

public class TestChunkSender implements WorldDataListener {
	
	private final Server server;
	
	public TestChunkSender(Server server) {
		this.server = server;
	}

	@Override
	public void onChunkLoaded(WorldData world, ChunkData chunk) {
		PacketLoadChunk packet = new PacketLoadChunk("Core:LoadChunk");
		
		packet.getPosition().set(
				chunk.getPosition().x,
				chunk.getPosition().y,
				chunk.getPosition().z
		);
		
		try {
			ChunkIO.save(chunk, packet.getData().getOutputStream());
		} catch (IOException e) {
			CrashReports.report(e, "TestChunkSender fjcked up. javahorse stupid");
		}
		
		server.getClientManager().broadcastLocal(packet, chunk.getPosition());
		
		tmp_sendPlayerIfPossible(world, chunk);
	}

	private void tmp_sendPlayerIfPossible(WorldData world, ChunkData chunk) {
		EntityData e = world.getEntity(TestContent.PLAYER_ENTITY_ID);
		if (e == null) return;
		
		if (Glm.equals(e.getChunkCoords(null), chunk.getPosition())) {
			System.out.printf("TestChunkSender: player found in (%d; %d; %d)\n", e.getChunkCoords(null).x, e.getChunkCoords(null).y, e.getChunkCoords(null).z);
			
			PacketSetLocalPlayer packet = new PacketSetLocalPlayer();
			packet.set(e.getEntityId());
			server.getClientManager().broadcastToAllPlayers(packet);
		}
	}

}
