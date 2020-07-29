/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client;

import ru.windcorp.optica.Proxy;
import ru.windcorp.optica.client.graphics.GUI;
import ru.windcorp.optica.client.graphics.backend.GraphicsBackend;
import ru.windcorp.optica.client.graphics.backend.RenderTaskQueue;
import ru.windcorp.optica.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.optica.client.graphics.world.LayerWorld;

public class ClientProxy implements Proxy {

	@Override
	public void initialize() {
		GraphicsBackend.initialize();
		try {
			RenderTaskQueue.waitAndInvoke(ShapeRenderProgram::init);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GUI.addBottomLayer(new LayerWorld());
	}

}
