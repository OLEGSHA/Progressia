package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import ru.windcorp.progressia.common.world.tile.PacketAddTile;

class AddTile extends CachedChunkChange<PacketAddTile> {

	public AddTile(Consumer<? super CachedChange> disposer) {
		super(disposer, new PacketAddTile());
	}

}