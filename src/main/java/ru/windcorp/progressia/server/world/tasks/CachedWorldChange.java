package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import ru.windcorp.progressia.common.comms.packets.PacketWorldChange;
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
		server.getClientManager().broadcastGamePacket(packet);
	}
	
	/**
	 * Invoked by both Change and Packet.
	 * @param world the world to affect
	 */
	protected abstract void affectCommon(WorldData world);

}
