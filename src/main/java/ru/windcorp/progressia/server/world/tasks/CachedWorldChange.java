package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.PacketAffectWorld;
import ru.windcorp.progressia.server.Server;

public abstract class CachedWorldChange<P extends PacketAffectWorld> extends CachedChange {
	
	private final P packet;

	public CachedWorldChange(Consumer<? super CachedChange> disposer, P packet) {
		super(disposer);
		this.packet = packet;
	}

	@Override
	public void affect(Server server) {
		affectLocal(server);
		sendPacket(server);
	}

	protected void affectLocal(Server server) {
		packet.apply(server.getWorld().getData());
	}
	
	protected void sendPacket(Server server) {
		Vec3i v = Vectors.grab3i();
		Vec3i chunkPos = getAffectedChunk(v);
		
		if (chunkPos == null) {
			server.getClientManager().broadcastToAllPlayers(packet);
		} else {
			server.getClientManager().broadcastLocal(packet, chunkPos);
		}
		
		Vectors.release(chunkPos);
	}

	protected Vec3i getAffectedChunk(Vec3i output) {
		return null;
	}
	
	public P getPacket() {
		return packet;
	}

}
