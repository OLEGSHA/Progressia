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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import ru.windcorp.progressia.client.world.block.BlockRenderOpaqueCube;
import ru.windcorp.progressia.client.world.block.BlockRenderRegistry;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;

public class Rocks {

	public enum RockType {
		IGNEOUS, METAMORPHIC, SEDIMENTARY;
	}

	public enum RockVariant {

		MONOLITH("Monolith"),
		CRACKED("Cracked"),
		GRAVEL("Gravel"),
		SAND("Sand");

		private final String name;

		private RockVariant(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	public static class Rock {

		private final String name;
		private final RockType type;

		private final Map<RockVariant, BlockData> blocks = new EnumMap<>(RockVariant.class);

		public Rock(String name, RockType type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public RockType getType() {
			return type;
		}
		
		public BlockData getBlock(RockVariant variant) {
			return blocks.get(variant);
		}
		
		public Collection<BlockData> getBlocks() {
			return blocks.values();
		}

		private void register() {
			for (RockVariant variant : RockVariant.values()) {

				String fullName = name + variant.getName();
				String id = "Test:" + fullName;

				BlockData blockData = new BlockData(id);
				blocks.put(variant, blockData);
				BlockDataRegistry.getInstance().register(blockData);
				BlockLogicRegistry.getInstance().register(new BlockLogic(id));
				BlockRenderRegistry.getInstance()
					.register(new BlockRenderOpaqueCube(id, BlockRenderRegistry.getBlockTexture(fullName)));

			}
		}

	}

	private final Map<String, Rock> rocksByName = Collections.synchronizedMap(new HashMap<>());
	private final Multimap<RockType, Rock> rocksByType = Multimaps.synchronizedMultimap(HashMultimap.create());
	
	public Rock create(RockType type, String name) {
		Rock rock = new Rock(name, type);
		rocksByName.put(name, rock);
		rocksByType.put(type, rock);
		return rock;
	}
	
	public void registerAllRocks() {
		getRocks().forEach(Rock::register);
	}
	
	public Collection<Rock> getRocks() {
		return rocksByName.values();
	}
	
	public Collection<Rock> getRocks(RockType type) {
		return rocksByType.get(type);
	}

}
