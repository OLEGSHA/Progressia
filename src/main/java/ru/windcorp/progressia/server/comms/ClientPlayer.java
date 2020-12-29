package ru.windcorp.progressia.server.comms;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.server.Player;

public abstract class ClientPlayer extends ClientChat {
	
	private Player player;

	public ClientPlayer(int id) {
		super(id);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public abstract String getLogin();
	
	public boolean isChunkVisible(Vec3i chunkPos) {
		if (player == null) return false;
		return player.getServer().getChunkManager().isChunkVisible(chunkPos, player);
	}
	
	public ChunkSet getVisibleChunks() {
		if (player == null) return ChunkSets.empty();
		return player.getServer().getChunkManager().getVisibleChunks(player);
	}
	
	public boolean isChunkVisible(long entityId) {
		return true;
	}

}
