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

import java.util.Collection;
import java.util.HashSet;

import ru.windcorp.progressia.client.comms.localhost.LocalServerCommsChannel;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.world.LayerWorld;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.ServerState;
import ru.windcorp.progressia.test.LayerAbout;
import ru.windcorp.progressia.test.LayerTestText;
import ru.windcorp.progressia.test.LayerTestUI;
import ru.windcorp.progressia.test.TestContent;

public class ClientState {

	private static Client instance;
	
	private static Collection<Layer> layers;
	
	private static boolean firstLoad;
	private static LayerTestText layer;

	public static Client getInstance() {
		return instance;
	}

	public static void setInstance(Client instance) {
		ClientState.instance = instance;
	}

	public static void connectToLocalServer() {

		WorldData world = new WorldData();

		LocalServerCommsChannel channel = new LocalServerCommsChannel(
			ServerState.getInstance()
		);
		
		firstLoad = true;

		Client client = new Client(world, channel);

		channel.connect(TestContent.PLAYER_LOGIN);

		setInstance(client);

		ServerState.getInstance().getChunkManager().register(bl -> {
			if (!bl && firstLoad)
			{
				MutableString t = new MutableStringLocalized("LayerText.Load");
				layer = new LayerTestText("Text",() -> {t.update(); return t.get();});
				GUI.addTopLayer(layer);
			}
			else if (bl && firstLoad)
			{
				GUI.removeLayer(layer);
				
				LayerWorld layerWorld = new LayerWorld(client);
				LayerTestUI layerUI = new LayerTestUI();
				LayerAbout layerAbout = new LayerAbout();
				GUI.addBottomLayer(layerWorld);
				GUI.addTopLayer(layerUI);
				GUI.addTopLayer(layerAbout);

				layers = new HashSet<Layer>();
				layers.add(layerWorld);
				layers.add(layerUI);
				layers.add(layerAbout);
			
				firstLoad = false;
			}
		});
		
	}
	
	public static void disconnectFromLocalServer()
	{
		for (Layer layer : layers)
		{
			GUI.removeLayer(layer);
		}
		
		ServerState.getInstance().getClientManager();
	}

	private ClientState() {
	}

}
