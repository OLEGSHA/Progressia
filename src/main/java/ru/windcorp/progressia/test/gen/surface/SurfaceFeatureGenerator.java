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
package ru.windcorp.progressia.test.gen.surface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.world.DefaultChunkData;

public class SurfaceFeatureGenerator {
	
	private final Surface surface;
	
	private final Collection<SurfaceFeature> features; // TODO make ordered
	
	public SurfaceFeatureGenerator(Surface surface, Collection<SurfaceFeature> features) {
		this.surface = surface;
		this.features = new ArrayList<>(features);
	}
	
	/**
	 * @return the surface
	 */
	public Surface getSurface() {
		return surface;
	}
	
	public void generateFeatures(DefaultChunkData chunk) {
		SurfaceWorld world = new SurfaceWorld(surface, chunk.getWorld());
		
		Random random = new Random(CoordinatePacker.pack3IntsIntoLong(chunk.getPosition()) /* ^ seed*/);
		SurfaceFeature.Request request = new SurfaceFeature.Request(world, chunk, random);
		
		for (SurfaceFeature feature : features) {
			feature.process(world, request);
		}
		
		chunk.setGenerationHint(true);
	}

}
