package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.PacketAffectChunk;

public abstract class CachedChunkChange<P extends PacketAffectChunk> extends CachedWorldChange<P> {

	public CachedChunkChange(Consumer<? super CachedChange> disposer, P packet) {
		super(disposer, packet);
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		getPacket().getAffectedChunk(output);
	}
	
	@Override
	protected Vec3i getAffectedChunk(Vec3i output) {
		getPacket().getAffectedChunk(output);
		return output;
	}

}
