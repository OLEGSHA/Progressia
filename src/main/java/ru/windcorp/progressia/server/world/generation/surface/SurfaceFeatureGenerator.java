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
package ru.windcorp.progressia.server.world.generation.surface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceContextImpl;

public class SurfaceFeatureGenerator {
	
	private final Surface surface;
	
	private final List<SurfaceFeature> features;
	
	public SurfaceFeatureGenerator(Surface surface, List<SurfaceFeature> features) {
		this.surface = surface;
		this.features = new ArrayList<>(features);
	}
	
	/**
	 * @return the surface
	 */
	public Surface getSurface() {
		return surface;
	}
	
	public void generateFeatures(Server server, DefaultChunkData chunk) {
		
		Random random = new Random(CoordinatePacker.pack3IntsIntoLong(chunk.getPosition()) /* ^ seed*/);
		
		SurfaceContextImpl context = new SurfaceContextImpl((ServerTileContext) server.createAbsoluteContext(), surface);
		context.setRandom(random);
		
		Vec3i tmpA = new Vec3i();
		Vec3i tmpB = new Vec3i();
		
		chunk.getMinBIW(tmpA);
		chunk.getMaxBIW(tmpB);
		
		context.toContext(tmpA, tmpA);
		context.toContext(tmpB, tmpB);
		
		Glm.min(tmpA, tmpB, context.getMin());
		Glm.max(tmpA, tmpB, context.getMax());
		
		for (SurfaceFeature feature : features) {
			feature.process(context);
		}
		
		chunk.setGenerationHint(true);
	}

}
