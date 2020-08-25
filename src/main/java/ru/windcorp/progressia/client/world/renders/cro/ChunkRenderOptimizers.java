/*******************************************************************************
 * Progressia
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
package ru.windcorp.progressia.client.world.renders.cro;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChunkRenderOptimizers {
	
	private ChunkRenderOptimizers() {}
	
	private static final Map<String, ChunkRenderOptimizerSupplier> SUPPLIERS =
			new HashMap<>();
	
	static {
		register(ChunkRenderOptimizerSupplier.of(
				"Default", "OpaqueCube",
				ChunkRenderOptimizerCube::new
		));
	}
	
	public static ChunkRenderOptimizerSupplier getSupplier(String id) {
		return SUPPLIERS.get(id);
	}
	
	public static void register(ChunkRenderOptimizerSupplier supplier) {
		SUPPLIERS.put(supplier.getId(), supplier);
	}
	
	public static Collection<ChunkRenderOptimizerSupplier> getAllSuppliers() {
		return SUPPLIERS.values();
	}
	
}
