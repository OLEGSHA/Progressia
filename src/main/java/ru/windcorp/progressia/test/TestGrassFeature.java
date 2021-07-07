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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;
import ru.windcorp.progressia.test.gen.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.test.gen.surface.SurfaceWorld;

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
	protected void processTopBlock(SurfaceWorld world, Request request, Vec3i topBlock) {
		if (!WHITELIST.contains(world.getBlockSfc(topBlock).getId())) {
			return;
		}
		
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		
		for (RelFace face : RelFace.getFaces()) {
			if (face == RelFace.DOWN) continue;
			
			if (BlockLogicRegistry.getInstance().get(world.getBlockSfc(topBlock.add_(face.getRelVector())).getId()).isTransparent()) {
				world.getTilesSfc(topBlock, face).addFarthest(grass);
			}
		}
	}

	@Override
	protected boolean isSolid(SurfaceWorld world, Vec3i surfaceBlockInWorld) {
		return BlockLogicRegistry.getInstance().get(world.getBlockSfc(surfaceBlockInWorld).getId()).isSolid(RelFace.UP);
	}

}
