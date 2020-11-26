package ru.windcorp.progressia.server.world.ticking;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.server.Server;

/**
 * A task that can be executed by a Ticker.
 * This is a superinterface for {@link Change} and {@link Evaluation} and is not meant to be extended further.
 * This interface is used to determine the Ticker that is suitable for the execution of this task.
 * @author javapony
 */
public abstract class TickerTask {
	
	/**
	 * Returns {@code false} iff this task is thread-safe and may be executed by
	 * any Ticker. If and only if a task returns {@code true} in this method
	 * is its {@link #getRelevantChunk(Vec3i)} method invoked.
	 * @implNote Default implementation returns {@code true}, making this task thread-sensitive
	 * @return {@code true} iff this task must be run in a Ticker implied by {@link #getRelevantChunk(Vec3i)}
	 */
	public boolean isThreadSensitive() {
		return true;
	}
	
	/**
	 * Sets {@code output} to be equal to the {@linkplain Coordinates#chunk coordinates of chunk}
	 * of the chunk that must be owned by the Ticker will execute this task. This method
	 * is not invoked iff {@link #isThreadSensitive()} returned {@code false}.
	 * @param output a {@link Vec3i} to set to the requested value
	 */
	public abstract void getRelevantChunk(Vec3i output);
	
	/**
	 * Invoked when this task has completed and will no longer be used.
	 * This method is guaranteed to be invoked in the main server thread.
	 * @implNote Default implementation does nothing
	 */
	public void dispose() {
		// Do nothing
	}
	
	/**
	 * Executes this task. This method is provided for the convenience of Tickers.
	 * @param server the server to run on
	 */
	abstract void run(Server server);

}
