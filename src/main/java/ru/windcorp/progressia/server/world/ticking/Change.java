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

import ru.windcorp.progressia.server.Server;

/**
 * A {@link TickerTask} that aims to perform a predetermined set of changes on
 * the world.
 * 
 * @author javapony
 */
public abstract class Change extends TickerTask {

	/**
	 * Performs the changes on the provided server instance.
	 * <p>
	 * This method will be executed when the world is in an inconsistent state
	 * and may not be queried, only changed. Therefore, all necessary inspection
	 * must be performed before this method is invoked, typically by an
	 * {@link Evaluation}. Failure to abide by this contract may lead to race
	 * conditions and/or devil invasions.
	 * 
	 * @param server
	 *            the {@link Server} instance to affect
	 */
	public abstract void affect(Server server);

	@Override
	void run(Server server) {
		affect(server);
	}

	@Override
	public int hashCode() {
		// Use instance hash code by default
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// Use instance-based equals() by default
		return super.equals(obj);
	}

}
