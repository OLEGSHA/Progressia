package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import ru.windcorp.progressia.server.world.ticking.Change;

public abstract class CachedChange extends Change {
	
	private final Consumer<? super CachedChange> disposer;
	
	public CachedChange(Consumer<? super CachedChange> disposer) {
		this.disposer = disposer;
	}

	@Override
	public void dispose() {
		disposer.accept(this);
	}
	
}