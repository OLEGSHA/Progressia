package ru.windcorp.progressia.server.world.ticking;

import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.tile.TickableTile;

/**
 * Various ticking policies that {@link TickableBlock} or {@link TickableTile} can have.
 * Ticking policy determines when, and if, the block or tile is ticked.
 * @author javapony
 */
public enum TickingPolicy {
	
	/**
	 * The ticking policy that requests that no ticks happen.
	 * This is typically used for blocks or tiles that only tick under certain conditions,
	 * which are not meant at the moment.
	 */
	NONE,
	
	/**
	 * The ticking policy that requests that the object is ticked every server tick exactly once.
	 * This should not be used for objects that only change rarely; consider using {@link RANDOM}
	 * instead.
	 */
	REGULAR,
	
	/**
	 * The ticking policy that requests that the object is ticked only once every
	 * <pre>
	 * Server.getTickingSettings().getRandomTickFrequency()</pre>
	 * seconds on average (this value is only determined at runtime). Note that
	 * the block might sometimes tick more than once per single server tick.
	 */
	RANDOM;

}
