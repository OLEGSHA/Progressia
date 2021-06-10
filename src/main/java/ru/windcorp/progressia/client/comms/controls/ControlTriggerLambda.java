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

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.comms.controls.ControlDataRegistry;
import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.util.namespaces.NamespacedUtil;

public class ControlTriggerLambda extends ControlTriggerInputBased {

	private final String packetId;
	private final Predicate<InputEvent> predicate;
	private final BiConsumer<InputEvent, ControlData> dataWriter;

	public ControlTriggerLambda(String id, Predicate<InputEvent> predicate,
			BiConsumer<InputEvent, ControlData> dataWriter) {
		super(id);

		this.packetId = NamespacedUtil.getId(NamespacedUtil.getNamespace(id),
				"ControlKeyPress" + NamespacedUtil.getName(id));

		this.predicate = predicate;
		this.dataWriter = dataWriter;
	}

	@Override
	public PacketControl onInputEvent(InputEvent event) {
		if (!predicate.test(event))
			return null;

		PacketControl packet = new PacketControl(packetId, ControlDataRegistry.getInstance().create(getId()));

		dataWriter.accept(event, packet.getControl());

		return packet;
	}

}
