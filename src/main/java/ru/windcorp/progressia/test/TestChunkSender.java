package ru.windcorp.progressia.test;

import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.PacketLoadChunk;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.server.Server;

public class TestChunkSender implements WorldDataListener {
	
	private final Server server;
	
	public TestChunkSender(Server server) {
		this.server = server;
	}

	@Override
	public void onChunkLoaded(WorldData world, ChunkData chunk) {
		PacketLoadChunk packet = new PacketLoadChunk("Core:LoadChunk");
		try {
			ChunkIO.save(chunk, packet.getData().getOutputStream());
		} catch (IOException e) {
			CrashReports.report(e, "TestChunkSender fjcked up. javahorse stupid");
		}
		server.getClientManager().broadcastGamePacket(packet);
	}

}
