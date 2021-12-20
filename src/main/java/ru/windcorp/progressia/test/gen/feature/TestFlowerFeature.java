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
package ru.windcorp.progressia.test.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.Flowers;
import ru.windcorp.progressia.test.Flowers.Flower;
import ru.windcorp.progressia.test.Flowers.FlowerVariant;
import ru.windcorp.progressia.test.TestContent;
import ru.windcorp.progressia.test.gen.Fields;
import ru.windcorp.progressia.test.gen.Fields.Field;

public class TestFlowerFeature extends SurfaceTopLayerFeature {

	private static class FlowerGenerator {
		
		private final TileData[] variants;
		private final SurfaceFloatField floweriness;

		public FlowerGenerator(Flower flower, Function<AbsFace, Field> flowerinessGenerator, Fields fields) {
			this.floweriness = fields.register(
				"Test:Flower" + flower.getName(),
				f -> Fields.rarify(flowerinessGenerator.apply(f), flower.getRarity())
			);
			
			List<TileData> tiles = new ArrayList<>();
			for (FlowerVariant variant : FlowerVariant.values()) {
				TileData tile = flower.getTile(variant);
				if (tile == null) {
					continue;
				}
				
				tiles.add(tile);
			}
			
			this.variants = tiles.toArray(new TileData[tiles.size()]);
		}

		public void generate(SurfaceBlockContext context) {
			float floweriness = this.floweriness.get(context);
			
			if (floweriness <= 0) {
				return;
			}
			
			float random = context.getRandom().nextFloat();
			
			int variant = (int) Math.floor((random + floweriness - 1) * variants.length);
			
			if (variant < 0) {
				return;
			} else if (variant >= variants.length) {
				// Screw doing float math properly, just clamp it
				variant = variants.length - 1;
			}
			
			context.addTile(RelFace.UP, variants[variant]);
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

	public TestFlowerFeature(String id, Flowers flowers, Function<AbsFace, Field> flowerinessGenerator, Fields fields) {
		super(id);

		this.flowers = flowers.getFlowers().stream()
			.map(flower -> new FlowerGenerator(flower, flowerinessGenerator, fields))
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
