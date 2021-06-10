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

package ru.windcorp.progressia.server.comms.controls;

import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.Client;

public abstract class ControlLogic extends Namespaced {

	@FunctionalInterface
	public static interface Lambda {
		void apply(Server server, PacketControl packet, Client client);
	}

	public ControlLogic(String id) {
		super(id);
	}

	public abstract void apply(Server server, PacketControl packet, Client client);

	public static ControlLogic of(String id, Lambda logic) {
		return new ControlLogic(id) {
			@Override
			public void apply(Server server, PacketControl packet, Client client) {
				logic.apply(server, packet, client);
			}
		};
	}

}
