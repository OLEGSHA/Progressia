/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.server.world.ticking;

import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.tile.TickableTile;

/**
 * Various ticking policies that {@link TickableBlock} or {@link TickableTile}
 * can have. Ticking policy determines when, and if, the block or tile is
 * ticked.
 * 
 * @author javapony
 */
public enum TickingPolicy {

	/**
	 * The ticking policy that requests that no ticks happen. This is typically
	 * used for blocks or tiles that only tick under certain conditions, which
	 * are not meant at the moment.
	 */
	NONE,

	/**
	 * The ticking policy that requests that the object is ticked every server
	 * tick exactly once. This should not be used for objects that only change
	 * rarely; consider using {@link RANDOM} instead.
	 */
	REGULAR,

	/**
	 * The ticking policy that requests that the object is ticked only once
	 * every
	 * 
	 * <pre>
	 * Server.getTickingSettings().getRandomTickFrequency()
	 * </pre>
	 * 
	 * seconds on average (this value is only determined at runtime). Note that
	 * the block might sometimes tick more than once per single server tick.
	 */
	RANDOM;

}
