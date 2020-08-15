/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client;

import ru.windcorp.progressia.Proxy;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.RenderTaskQueue;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.flat.LayerTestUI;
import ru.windcorp.progressia.client.graphics.gui.LayerTestGUI;
import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.world.LayerWorld;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.renders.BlockRenders;

public class ClientProxy implements Proxy {

	@Override
	public void initialize() {
		GraphicsBackend.initialize();
		try {
			RenderTaskQueue.waitAndInvoke(FlatRenderProgram::init);
			RenderTaskQueue.waitAndInvoke(WorldRenderProgram::init);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BlockRenders.registerTest();
		Atlases.loadAllAtlases();
		
		GUI.addBottomLayer(new LayerWorld());
		GUI.addTopLayer(new LayerTestUI());
		GUI.addTopLayer(new LayerTestGUI());
	}

}
