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
package ru.windcorp.progressia.server.world.context;

import java.util.Random;

import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.ServerState;

/**
 * A server-side {@link Context}. This context has a {@link Server} instance.
 */
public interface ServerContext extends Context {

	/**
	 * Gets the {@link Server} instance relevant to this context. This method
	 * should always be preferred to {@link ServerState#getInstance()} when
	 * possible.
	 * 
	 * @return the server instance
	 */
	Server getServer();

	/**
	 * Retrieves a context-appropriate source of randomness. This source should
	 * always be preferred to any other when possible.
	 * 
	 * @return an intended {@link Random} instance
	 */
	Random getRandom();

	/**
	 * Returns the duration of the last server tick. Server logic should assume
	 * that this much in-world time has passed.
	 * 
	 * @return the length of the last server tick
	 */
	double getTickLength();

	/**
	 * Adjusts the provided value according to tick length assuming the value
	 * scales linearly. The call {@code ctxt.adjustValue(x)} is equivalent to
	 * {@code ((float) ctxt.getTickLength()) * x}.
	 * 
	 * @param valueForOneSecond the value to adjust, normalized to one second
	 * @return the value adjust to account for the actual tick length
	 * @see #getTickLength()
	 */
	default float adjustTime(float valueForOneSecond) {
		return ((float) getTickLength()) * valueForOneSecond;
	}

	/**
	 * Adjusts the provided value according to tick length assuming the value
	 * scales linearly. The call {@code ctxt.adjustValue(x)} is equivalent to
	 * {@code ctxt.getTickLength() * x}.
	 * 
	 * @param valueForOneSecond the value to adjust, normalized to one second
	 * @return the value adjust to account for the actual tick length
	 * @see #getTickLength()
	 */
	default double adjustTime(double valueForOneSecond) {
		return getTickLength() * valueForOneSecond;
	}

}
