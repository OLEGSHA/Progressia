/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.world.renders.bro;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockRenderOptimizerGenerators {
	
	private BlockRenderOptimizerGenerators() {}
	
	private static final Map<String, BlockRenderOptimizerGenerator> GENERATORS =
			new HashMap<>();
	
	static {
		register(new BlockRenderOptimizerGenerator("Default", "OpaqueCube") {
			@Override
			public BlockRenderOptimizer createOptimizer() {
				return new BlockRenderOpaqueCubeOptimizer();
			}
		});
	}
	
	public static BlockRenderOptimizerGenerator get(String id) {
		return GENERATORS.get(id);
	}
	
	public static void register(BlockRenderOptimizerGenerator generator) {
		GENERATORS.put(generator.getId(), generator);
	}
	
	public static Collection<BlockRenderOptimizerGenerator> getAll() {
		return GENERATORS.values();
	}
	
}
