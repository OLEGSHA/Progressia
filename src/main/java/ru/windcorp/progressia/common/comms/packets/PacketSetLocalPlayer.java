package ru.windcorp.progressia.common.comms.packets;

public class PacketSetLocalPlayer extends Packet {
	
	private long localPlayerEntityId;

	public PacketSetLocalPlayer(long entityId) {
		this("Core:SetLocalPlayer", entityId);
	}
	
	protected PacketSetLocalPlayer(String id, long entityId) {
		super(id);
		this.localPlayerEntityId = entityId;
	}
	
	public long getLocalPlayerEntityId() {
		return localPlayerEntityId;
	}
	
	public void setLocalPlayerEntityId(long localPlayerEntityId) {
		this.localPlayerEntityId = localPlayerEntityId;
	}

}
