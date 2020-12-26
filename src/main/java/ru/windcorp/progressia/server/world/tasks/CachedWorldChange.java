package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.comms.packets.PacketWorldChange;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.Server;

public abstract class CachedWorldChange extends CachedChange {
	
	private final PacketWorldChange packet;

	public CachedWorldChange(Consumer<? super CachedChange> disposer, String packetId) {
		super(disposer);
		
		this.packet = new PacketWorldChange(packetId) {
			@Override
			public void apply(WorldData world) {
				affectCommon(world);
			}
		};
	}

	@Override
	public void affect(Server server) {
		affectCommon(server.getWorld().getData());
		
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
	
	/**
	 * Invoked by both Change and Packet.
	 * @param world the world to affect
	 */
	protected abstract void affectCommon(WorldData world);

}
