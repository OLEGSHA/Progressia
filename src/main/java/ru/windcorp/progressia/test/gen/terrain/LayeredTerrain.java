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
package ru.windcorp.progressia.test.gen.terrain;

import java.util.ArrayList;
import java.util.List;

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.server.world.generation.surface.TerrainSupplier;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;

public class LayeredTerrain implements TerrainSupplier {
	
	private final List<TerrainLayer> layers = new ArrayList<>();
	
	public void addLayer(TerrainLayer layer) {
		this.layers.add(layer);
	}

	@Override
	public BlockData get(SurfaceBlockContext context, float depth) {
		TerrainLayer layer = null;
		float intensity = 0;
		
		for (int i = 0; i < layers.size(); ++i) {
			TerrainLayer currentLayer = layers.get(i);
			
			float currentIntensity = currentLayer.getIntensity(context, depth);
			if (currentIntensity <= 0) {
				continue;
			}
			
			if (intensity < currentIntensity) {
				intensity = currentIntensity;
				layer = currentLayer;
			}
		}
		
		if (layer == null) {
			layer = layers.get(0);
		}
		
		return layer.generate(context, depth, intensity);
	}

}
