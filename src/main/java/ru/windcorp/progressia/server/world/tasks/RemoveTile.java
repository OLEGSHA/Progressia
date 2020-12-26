package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import ru.windcorp.progressia.common.world.tile.PacketRemoveTile;

class RemoveTile extends CachedChunkChange<PacketRemoveTile> {

	public RemoveTile(Consumer<? super CachedChange> disposer) {
		super(disposer, new PacketRemoveTile());
	}

}