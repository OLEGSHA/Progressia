package ru.windcorp.progressia.common.comms.packets;

public class PacketSetLocalPlayer extends Packet {
	
	private long localPlayerEntityId;

	public PacketSetLocalPlayer(long entityId) {
		super("Core", "SetLocalPlayer");
		this.localPlayerEntityId = entityId;
	}
	
	public long getLocalPlayerEntityId() {
		return localPlayerEntityId;
	}
	
	public void setLocalPlayerEntityId(long localPlayerEntityId) {
		this.localPlayerEntityId = localPlayerEntityId;
	}

}
