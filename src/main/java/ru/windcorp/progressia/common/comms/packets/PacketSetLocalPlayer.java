package ru.windcorp.progressia.common.comms.packets;

import java.util.UUID;

public class PacketSetLocalPlayer extends Packet {
	
	private final UUID localPlayerEntityUUID;

	public PacketSetLocalPlayer(UUID uuid) {
		super("Core", "SetLocalPlayer");
		this.localPlayerEntityUUID = uuid;
	}
	
	public UUID getLocalPlayerEntityUUID() {
		return localPlayerEntityUUID;
	}

}
