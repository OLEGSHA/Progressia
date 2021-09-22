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
package ru.windcorp.progressia.test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.world.tile.TileRenderRegistry;
import ru.windcorp.progressia.client.world.tile.TileRenderTransparentSurface;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.tile.HangingTileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;

public class Flowers {

	private static final int HERB_MAX_COUNT = 3;
	private static final int TINY_MAX_COUNT = 8;
	private static final float TINY_SIZE = 0.5f;

	public enum FlowerVariant {

		FLAT("Flat"),
		TINY("Tiny"),
		HERB("Herb");

		private final String name;

		private FlowerVariant(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	public static class Flower {

		private final String name;

		/**
		 * 1 disables spawning, 0 spawns everywhere
		 */
		private final float rarity;

		private final FlowerVariant[] requestedVariants;
		private final Map<FlowerVariant, TileData> tiles = new HashMap<>();

		public Flower(String name, float rarity, FlowerVariant... variants) {
			Objects.requireNonNull(name, "name");
			Objects.requireNonNull(variants, "variants");

			this.name = name;
			this.rarity = rarity;
			this.requestedVariants = variants;
		}

		public String getName() {
			return name;
		}
		
		public float getRarity() {
			return rarity;
		}

		public TileData getTile(FlowerVariant variant) {
			return tiles.get(variant);
		}

		public Collection<TileData> getTiles() {
			return tiles.values();
		}

		private void register() {
			for (FlowerVariant variant : requestedVariants) {

				String fullName = "Flower" + name + variant.getName();
				String id = "Test:" + fullName;

				TileData tileData = new TileData(id);
				tiles.put(variant, tileData);
				TileDataRegistry.getInstance().register(tileData);

				Texture texture = TileRenderRegistry.getTileTexture(fullName);

				TileLogicRegistry logic = TileLogicRegistry.getInstance();
				TileRenderRegistry render = TileRenderRegistry.getInstance();

				switch (variant) {
				case HERB:
					logic.register(new HangingTileLogic(id));
					render.register(new TileRenderHerb(id, texture, HERB_MAX_COUNT));
					break;
				case TINY:
					logic.register(new HangingTileLogic(id));
					render.register(new TileRenderTinyFlower(id, texture, TINY_MAX_COUNT, TINY_SIZE));
					break;
				case FLAT:
				default:
					logic.register(new TestTileLogicGrass(id));
					render.register(new TileRenderTransparentSurface(id, texture));
					break;
				}

			}
		}

	}

	private final Map<String, Flower> flowersByName = Collections.synchronizedMap(new HashMap<>());

	public Flower create(String name, float rarity, FlowerVariant... variants) {
		Flower flower = new Flower(name, rarity, variants);
		flowersByName.put(name, flower);
		return flower;
	}

	public void registerAllFlowers() {
		getFlowers().forEach(Flower::register);
	}

	public Collection<Flower> getFlowers() {
		return flowersByName.values();
	}

}
