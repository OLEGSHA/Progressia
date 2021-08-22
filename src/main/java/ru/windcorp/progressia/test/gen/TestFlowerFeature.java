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
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.TestContent;

public class TestFlowerFeature extends SurfaceTopLayerFeature {

	private static class FlowerGenerator {
		private final TileData tile;
		private final SurfaceFloatField floweriness;

		public FlowerGenerator(TileData tile, Function<String, SurfaceFloatField> flowerinessGenerator) {
			this.tile = tile;
			this.floweriness = flowerinessGenerator.apply(tile.getName());
		}

		public void generate(SurfaceBlockContext context) {
			if (context.getRandom().nextDouble() < floweriness.get(context)) {
				context.addTile(RelFace.UP, tile);
			}
		}
	}

	private final Set<String> soilWhitelist;
	{
		ImmutableSet.Builder<String> b = ImmutableSet.builder();
		b.add("Test:Dirt", "Test:Stone");
		TestContent.ROCKS.getRocks().forEach(rock -> rock.getBlocks().forEach(block -> b.add(block.getId())));
		soilWhitelist = b.build();
	}

	private final FlowerGenerator[] flowers;

	public TestFlowerFeature(String id, Function<String, SurfaceFloatField> flowerinessGenerator) {
		super(id);

		this.flowers = TileDataRegistry.getInstance().values().stream()
			.filter(tile -> tile.getName().endsWith("Flowers"))
			.map(tile -> new FlowerGenerator(tile, flowerinessGenerator))
			.toArray(FlowerGenerator[]::new);
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

		for (FlowerGenerator flower : flowers) {
			flower.generate(context);
		}
	}

	@Override
	protected boolean isSolid(SurfaceBlockContext context) {
		return context.logic().getBlock().isSolid(RelFace.UP);
	}

}
