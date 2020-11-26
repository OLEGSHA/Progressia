package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import ru.windcorp.progressia.server.world.ticking.Evaluation;

public abstract class CachedEvaluation extends Evaluation {
	
	private final Consumer<? super CachedEvaluation> disposer;
	
	public CachedEvaluation(Consumer<? super CachedEvaluation> disposer) {
		this.disposer = disposer;
	}

	@Override
	public void dispose() {
		disposer.accept(this);
	}
	
}