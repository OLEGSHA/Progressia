package ru.windcorp.progressia.server.world.tasks;

import java.util.Objects;
import java.util.function.Consumer;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.tile.PacketAffectTile;

public class CachedTileChange<P extends PacketAffectTile> extends CachedChunkChange<P> {

	public CachedTileChange(Consumer<? super CachedChange> disposer, P packet) {
		super(disposer, packet);
	}
	
	@Override
	public int hashCode() {
		PacketAffectTile packet = getPacket();
		Vec3i biw = packet.getBlockInWorld();
		
		final int prime = 31;
		int result = 1;
		result = prime * result + biw.x;
		result = prime * result + biw.y;
		result = prime * result + biw.z;
		result = prime * result + Objects.hashCode(packet.getFace());
		result = prime * result + packet.getTag();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CachedTileChange<?>)) return false;
		
		PacketAffectTile my = getPacket();
		PacketAffectTile other = ((CachedTileChange<?>) obj).getPacket();
		
		// Tag of -1 signals that we should ignore it
		if (my.getTag() == -1 || other.getTag() == -1) return false;
		
		return Glm.equals(my.getBlockInWorld(), other.getBlockInWorld())
				&& (my.getFace() == other.getFace())
				&& (my.getTag() == other.getTag());
	}
	
	@Override
	public String toString() {
		PacketAffectTile packet = getPacket();
		Vec3i biw = packet.getBlockInWorld();
		
		return getClass().getSimpleName() + " (" + biw.x + "; " + biw.y + "; " + biw.z + "; " + packet.getFace() + "; tag: " + packet.getTag() + ")";
	}

}
