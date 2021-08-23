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
package ru.windcorp.progressia.test.gen;

import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

import glm.vec._3.i.Vec3i;
import kdotjpg.opensimplex2.areagen.OpenSimplex2S;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceWorldContext;

public abstract class MultiblockVegetationFeature extends SurfaceTopLayerFeature {

	private final SurfaceFloatField selector;
	private final OpenSimplex2S wavinessGenerator = new OpenSimplex2S(0);

	private final double maximumDensity;

	private final Set<String> soilWhitelist;

	public MultiblockVegetationFeature(String id, SurfaceFloatField selector, double minimumPeriod) {
		super(id);
		this.selector = selector;
		this.maximumDensity = 1 / minimumPeriod;

		ImmutableSet.Builder<String> soilWhitelistBuilder = ImmutableSet.builder();
		createSoilWhitelist(soilWhitelistBuilder::add);
		this.soilWhitelist = soilWhitelistBuilder.build();
	}

	protected void createSoilWhitelist(Consumer<String> output) {
		output.accept("Test:Dirt");
	}

	@Override
	protected void processTopBlock(SurfaceBlockContext context) {
		Vec3i location = context.getLocation();

		if (location.z < 0) {
			return;
		}

		if (!soilWhitelist.isEmpty() && !soilWhitelist.contains(context.getBlock().getId())) {
			return;
		}

		double selectorValue = selector.get(context);
		double chance = selectorValue * maximumDensity;
		if (context.getRandom().nextDouble() >= chance) {
			return;
		}

		grow(context, selectorValue);
	}

	protected abstract void grow(SurfaceBlockContext context, double selectorValue);

	@Override
	protected boolean isSolid(SurfaceBlockContext context) {
		return context.logic().getBlock().isSolid(RelFace.UP);
	}

	/*
	 * Utilities
	 */

	protected void setLeaves(SurfaceWorldContext context, Vec3i location, BlockData leaves) {
		if (context.getBlock(location).getId().equals("Test:Air")) {
			context.setBlock(location, leaves);
		}
	}

	protected void iterateBlob(
		Vec3i center,
		double horDiameter,
		double vertDiameter,
		double wavinessAmplitude,
		double wavinessScale,
		Consumer<Vec3i> action
	) {
		VectorUtil.iterateCuboidAround(
			center.x,
			center.y,
			center.z,
			(int) Math.ceil(horDiameter) / 2 * 2 + 5,
			(int) Math.ceil(horDiameter) / 2 * 2 + 5,
			(int) Math.ceil(vertDiameter) / 2 * 2 + 5,
			pos -> {
				
				double sx = (pos.x - center.x) / horDiameter;
				double sy = (pos.y - center.y) / horDiameter;
				double sz = (pos.z - center.z) / vertDiameter;
				
				double radius = 1;
				
				if (wavinessAmplitude > 0) {
					radius += wavinessAmplitude * wavinessGenerator.noise3_Classic(
						sx / wavinessScale,
						sy / wavinessScale,
						sz / wavinessScale
					);
				}

				if (sx * sx + sy * sy + sz * sz <= radius * radius) {
					action.accept(pos);
				}
				
			}
		);
	}
	
	protected void iterateSpheroid(
		Vec3i center,
		double horDiameter,
		double vertDiameter,
		Consumer<Vec3i> action
	) {
		iterateBlob(center, horDiameter, vertDiameter, 0, 0, action);
	}
	
	protected void iterateSphere(Vec3i center, double diameter, Consumer<Vec3i> action) {
		iterateBlob(center, diameter, diameter, 0, 0, action);
	}

}
