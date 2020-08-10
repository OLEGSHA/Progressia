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
package ru.windcorp.progressia.client.world.renders.bro;

import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.client.world.renders.BlockRender;

public abstract class BlockRenderOptimizer {
	
	public abstract void startRender(ChunkRender chunk);
	
	public abstract void processBlock(BlockRender block, int x, int y, int z);
	
	public abstract Shape endRender();

}
