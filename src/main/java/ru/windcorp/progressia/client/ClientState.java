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

package ru.windcorp.progressia.client;

import ru.windcorp.progressia.client.comms.localhost.LocalServerCommsChannel;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.world.LayerWorld;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.server.ServerState;
import ru.windcorp.progressia.test.LayerAbout;
import ru.windcorp.progressia.test.LayerTestText;
import ru.windcorp.progressia.test.LayerTestUI;
import ru.windcorp.progressia.test.TestContent;

public class ClientState {

	private static Client instance;

	public static Client getInstance() {
		return instance;
	}

	public static void setInstance(Client instance) {
		ClientState.instance = instance;
	}

	public static void connectToLocalServer() {

		DefaultWorldData world = new DefaultWorldData();

		LocalServerCommsChannel channel = new LocalServerCommsChannel(
			ServerState.getInstance()
		);

		Client client = new Client(world, channel);

		channel.connect(TestContent.PLAYER_LOGIN);

		setInstance(client);
		displayLoadingScreen();

	}

	private static void displayLoadingScreen() {
		GUI.addTopLayer(new LayerTestText("Text", new MutableStringLocalized("LayerText.Load"), layer -> {
			Client client = ClientState.getInstance();

			// TODO refacetor and remove
			if (client != null) {
				client.getComms().processPackets();
			}
			
			if (client != null && client.getLocalPlayer().hasEntity()) {
				GUI.removeLayer(layer);

				// TODO refactor, this shouldn't be here
				LayerWorld layerWorld = new LayerWorld(client);
				LayerTestUI layerUI = new LayerTestUI();
				LayerAbout layerAbout = new LayerAbout();
				GUI.addBottomLayer(layerWorld);
				GUI.addTopLayer(layerUI);
				GUI.addTopLayer(layerAbout);
			}
		}));
	}

	public static void disconnectFromLocalServer() {
		getInstance().getComms().disconnect();
		
		for (Layer layer : GUI.getLayers()) {
			GUI.removeLayer(layer);
		}
	}

	private ClientState() {
	}

}
