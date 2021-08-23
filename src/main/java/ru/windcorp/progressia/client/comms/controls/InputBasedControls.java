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

package ru.windcorp.progressia.client.comms.controls;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.comms.packets.Packet;

public class InputBasedControls {

	private final Client client;

	private final ControlTriggerInputBased[] controls;

	public InputBasedControls(Client client) {
		this.client = client;

		this.controls = ControlTriggerRegistry.getInstance().values().stream()
				.filter(ControlTriggerInputBased.class::isInstance).toArray(ControlTriggerInputBased[]::new);
	}

	public void handleInput(Input input) {
		for (ControlTriggerInputBased c : controls) {
			Packet packet = c.onInputEvent(input.getEvent());

			if (packet != null) {
				input.consume();
				client.getComms().sendPacket(packet);
				break;
			}
		}
	}

}
