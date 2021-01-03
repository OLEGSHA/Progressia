package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import ru.windcorp.progressia.common.world.block.PacketSetBlock;

class SetBlock extends CachedBlockChange<PacketSetBlock> {

	public SetBlock(Consumer<? super CachedChange> disposer) {
		super(disposer, new PacketSetBlock());
	}

}