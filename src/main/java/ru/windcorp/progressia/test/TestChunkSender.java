package ru.windcorp.progressia.test;

import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.PacketRevokeChunk;
import ru.windcorp.progressia.common.world.PacketSendChunk;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.ClientPlayer;

public class TestChunkSender {
	
	public static void sendChunk(Server server, ClientPlayer receiver, Vec3i chunkPos) {
		ChunkData chunk = server.getWorld().getData().getChunk(chunkPos);
		
		if (chunk == null) {
			throw new IllegalStateException(String.format(
					"Chunk (%d; %d; %d) is not loaded, cannot send",
					chunkPos.x, chunkPos.y, chunkPos.z
			));
		}
		
		PacketSendChunk packet = new PacketSendChunk();
		packet.getPosition().set(chunkPos.x, chunkPos.y, chunkPos.z);
		
		try {
			ChunkIO.save(chunk, packet.getData().getOutputStream());
		} catch (IOException e) {
			CrashReports.report(e, "TestChunkSender fjcked up. javahorse stupid");
		}
		
		receiver.sendPacket(packet);
	}

	public static void revokeChunk(ClientPlayer receiver, Vec3i chunkPos) {
		PacketRevokeChunk packet = new PacketRevokeChunk();
		packet.set(chunkPos);
		receiver.sendPacket(packet);
	}

}
