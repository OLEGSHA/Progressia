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
 * A {@link TickerTask} that needs to access the world for analysis.
 * 
 * @author javapony
 */
public abstract class Evaluation extends TickerTask {

	/**
	 * Performs the analysis of the provided server instance.
	 * <p>
	 * This method will be executed when the world is in an consistent state and
	 * may be queried for meaningful information. However, other evaluations may
	 * be happening concurrently, so any world modification is prohibited.
	 * Evaluations are expected to request {@link Change}s to interact with the
	 * world. Failure to abide by this contract may lead to race conditions
	 * and/or devil invasions.
	 * 
	 * @param server
	 *            the server instance to inspect
	 */
	public abstract void evaluate(Server server);

	@Override
	void run(Server server) {
		evaluate(server);
	}

}
