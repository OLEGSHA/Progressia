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

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.test.gen.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceBlockContext;

public class TestGrassFeature extends SurfaceTopLayerFeature {
	
	private static final Set<String> WHITELIST = ImmutableSet.of(
		"Test:Dirt",
		"Test:Stone",
		"Test:GraniteMonolith",
		"Test:GraniteCracked",
		"Test:GraniteGravel"
	);

	public TestGrassFeature(String id) {
		super(id);
	}

	@Override
	protected void processTopBlock(SurfaceBlockContext context) {
		if (!WHITELIST.contains(context.getBlock().getId())) {
			return;
		}
		
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		
		for (RelFace face : RelFace.getFaces()) {
			if (face == RelFace.DOWN) continue;
			
			if (context.pushRelative(face).logic().getBlock().isTransparent()) {
				context.pop();
				context.addTile(face, grass);
			} else {
				context.pop();
			}
			
		}
	}

	@Override
	protected boolean isSolid(SurfaceBlockContext context) {
		return context.logic().getBlock().isSolid(RelFace.UP);
	}

}
