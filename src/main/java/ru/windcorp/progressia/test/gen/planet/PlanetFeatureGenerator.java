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
package ru.windcorp.progressia.test.gen.planet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.test.gen.surface.SurfaceFeature;
import ru.windcorp.progressia.test.gen.surface.SurfaceFeatureGenerator;

public class PlanetFeatureGenerator {
	
	private final TestPlanetGenerator parent;
	private final SurfaceFeatureGenerator surfaceGenerator;

	public PlanetFeatureGenerator(TestPlanetGenerator generator) {
		this.parent = generator;
		
		Collection<SurfaceFeature> features = new ArrayList<>();
		features.add(new SurfaceFeature("Test:GlassFeature") {
			@Override
			public void process(ChunkData chunk, Random random) {
				
				chunk.setBlockRel(new Vec3i(8, 8, 8), BlockDataRegistry.getInstance().get("Test:Glass"), true);
				
			}
		});
		
		this.surfaceGenerator = new SurfaceFeatureGenerator(features);
	}
	
	public TestPlanetGenerator getGenerator() {
		return parent;
	}
	
	public void generateFeatures(ChunkData chunk) {
		if (isOrdinaryChunk(chunk.getPosition())) {
			generateOrdinaryFeatures(chunk);
		} else {
			generateBorderFeatures(chunk);
		}
	}

	private boolean isOrdinaryChunk(Vec3i chunkPos) {
		Vec3i sorted = VectorUtil.sortAfterAbs(chunkPos, null);
		return sorted.x != sorted.y;
	}

	private void generateOrdinaryFeatures(ChunkData chunk) {
		surfaceGenerator.generateFeatures(chunk);
	}

	private void generateBorderFeatures(ChunkData chunk) {
		// Do nothing
		chunk.setGenerationHint(true);
	}

}
