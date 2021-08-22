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

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import ru.windcorp.progressia.common.util.ArrayFloatRangeMap;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.TestContent;

public class TestGrassFeature extends SurfaceTopLayerFeature {

	private final Set<String> soilWhitelist;
	{
		ImmutableSet.Builder<String> b = ImmutableSet.builder();
		b.add("Test:Dirt", "Test:Stone");
		TestContent.ROCKS.getRocks().forEach(rock -> rock.getBlocks().forEach(block -> b.add(block.getId())));
		soilWhitelist = b.build();
	}

	private final SurfaceFloatField grassiness;
	private final double scatterDensity = 1.0 / (3 * 3);

	private final TileData grass = TileDataRegistry.getInstance().get("Test:Grass");

	private final FloatRangeMap<TileData> grasses = new ArrayFloatRangeMap<>();
	{
		grasses.put(0.6f, 1, TileDataRegistry.getInstance().get("Test:TallGrass"));
		grasses.put(0.4f, 0.6f, TileDataRegistry.getInstance().get("Test:MediumGrass"));
		grasses.put(0.1f, 0.4f, TileDataRegistry.getInstance().get("Test:LowGrass"));
	}

	private final List<TileData> scatter = ImmutableList.of(
		TileDataRegistry.getInstance().get("Test:Stones"),
		TileDataRegistry.getInstance().get("Test:Sand"),
		TileDataRegistry.getInstance().get("Test:Bush")
	);

	public TestGrassFeature(String id, SurfaceFloatField grassiness) {
		super(id);
		this.grassiness = grassiness;
	}

	@Override
	protected void processTopBlock(SurfaceBlockContext context) {
		if (context.getLocation().z < 0) {
			return;
		}
		if (!soilWhitelist.contains(context.getBlock().getId())) {
			return;
		}

		if (!context.pushRelative(RelFace.UP).logic().getBlock().isTransparent()) {
			context.pop();
			return;
		}
		context.pop();

		double grassiness = this.grassiness.get(context);
		if (grassiness > 0.1) {
			growGrass(context, grassiness);
		}

		placeScatter(context);
	}

	private void placeScatter(SurfaceBlockContext context) {
		if (context.getRandom().nextDouble() < scatterDensity) {
			TileData tile = pickRandom(context, scatter);
			context.addTile(RelFace.UP, tile);
		}
	}

	private void growGrass(SurfaceBlockContext context, double grassiness) {
		for (RelFace face : RelFace.getFaces()) {
			if (face == RelFace.DOWN)
				continue;

			if (context.pushRelative(face).logic().getBlock().isTransparent()) {
				context.pop();
				context.addTile(face, grass);
			} else {
				context.pop();
			}

		}

		if (context.getRandom().nextDouble() < grassiness) {
			TileData herbGrass = grasses.get((float) grassiness);
			if (herbGrass != null) {
				context.addTile(RelFace.UP, herbGrass);
			}
		}
	}

	@Override
	protected boolean isSolid(SurfaceBlockContext context) {
		return context.logic().getBlock().isSolid(RelFace.UP);
	}

}
