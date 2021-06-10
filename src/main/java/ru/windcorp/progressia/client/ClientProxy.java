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

import ru.windcorp.progressia.Proxy;
import ru.windcorp.progressia.client.audio.AudioSystem;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.RenderTaskQueue;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.font.GNUUnifontLoader;
import ru.windcorp.progressia.client.graphics.font.Typefaces;
import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.server.ServerState;
import ru.windcorp.progressia.test.TestContent;
import ru.windcorp.progressia.test.TestMusicPlayer;

public class ClientProxy implements Proxy {

	@Override
	public void initialize() {
		GraphicsBackend.initialize();
		try {
			RenderTaskQueue.waitAndInvoke(FlatRenderProgram::init);
			RenderTaskQueue.waitAndInvoke(WorldRenderProgram::init);
			RenderTaskQueue.waitAndInvoke(() -> Typefaces
					.setDefault(GNUUnifontLoader.load(ResourceManager.getResource("assets/unifont-13.0.03.hex.gz"))));
		} catch (InterruptedException e) {
			throw CrashReports.report(e, "ClientProxy failed");
		}

		Localizer.getInstance().setLanguage("en-US");

		TestContent.registerContent();

		Atlases.loadAllAtlases();

		AudioSystem.initialize();

		ServerState.startServer();
		ClientState.connectToLocalServer();

		TestMusicPlayer.start();
	}

}
