package ru.windcorp.progressia.server.world.tasks;

import java.io.IOException;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.PacketEntityChange;
import ru.windcorp.progressia.server.Server;

class ChangeEntity extends CachedChange {

	private EntityData entity;
	private StateChange<?> change;

	private final PacketEntityChange packet = new PacketEntityChange();

	public ChangeEntity(Consumer<? super CachedChange> disposer) {
		super(disposer);
	}

	public <T extends EntityData> void set(T entity, StateChange<T> change) {
		if (this.entity != null)
			throw new IllegalStateException("Entity is not null. Current: " + this.entity + "; requested: " + entity);
		
		if (this.change != null)
			throw new IllegalStateException("Change is not null. Current: " + this.change + "; requested: " + change);
		
		this.entity = entity;
		this.change = change;

		packet.setEntityId(entity.getEntityId());
		try {
			entity.write(packet.getWriter(), IOContext.COMMS); // TODO wtf is this... (see whole file)
		} catch (IOException e) {
			CrashReports.report(e, "Could not write entity %s", entity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void affect(Server server) {
		((StateChange<EntityData>) change).change(entity);

		try {
			entity.write(packet.getWriter(), IOContext.COMMS); // ...and this doing at the same time? - javapony at 1 AM
		} catch (IOException e) {
			CrashReports.report(e, "Could not write entity %s", entity);
		}
		
		server.getClientManager().broadcastGamePacket(packet);
	}
	
	@Override
	public void getRelevantChunk(Vec3i output) {
		// Do nothing
	}
	
	@Override
	public boolean isThreadSensitive() {
		return false;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.entity = null;
		this.change = null;
	}

}