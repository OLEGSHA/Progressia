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
package ru.windcorp.progressia.test.gen.planet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.generation.AbstractWorldGenerator;

public class TestPlanetGenerator extends AbstractWorldGenerator<Boolean> {
	
	private final int surfaceLevel;

	public TestPlanetGenerator(String id, float planetRadius, WorldLogic world) {
		super(id, Boolean.class, "Test:PlanetGravityModel");
		
		this.surfaceLevel = (int) (planetRadius / ChunkData.BLOCKS_PER_CHUNK);
		if (surfaceLevel < 2) {
			throw new IllegalArgumentException("planetRadius too small, must be at least 32 m");
		}
	}
	
	@Override
	public Vec3 suggestSpawnLocation() {
		return new Vec3(0, 0, 66);
	}

	@Override
	protected Boolean doReadGenerationHint(DataInputStream input) throws IOException, DecodingException {
		return input.readBoolean();
	}

	@Override
	protected void doWriteGenerationHint(DataOutputStream output, Boolean hint) throws IOException {
		output.writeBoolean(hint);
	}

	@Override
	protected boolean checkIsChunkReady(Boolean hint) {
		return hint;
	}

	@Override
	public ChunkData generate(Vec3i chunkPos, WorldData world) {
		ChunkData chunk = new ChunkData(chunkPos, world);
		
		generate(chunk);
		chunk.setGenerationHint(true);
		
		return chunk;
	}
	
	private enum ChunkType {
		SURFACE, UNDERGROUND, EDGE_SURFACE, EDGE_UNDERGROUND, CORE, AIR;
	}

	private void generate(ChunkData chunk) {
		switch (getChunkType(chunk.getPosition())) {
		case SURFACE:
			fillSurface(chunk);
			break;
		case UNDERGROUND:
			fillUndeground(chunk);
			break;
		case EDGE_SURFACE:
			fillEdgeSurface(chunk);
			break;
		case EDGE_UNDERGROUND:
			fillEdgeUnderground(chunk);
			break;
		case CORE:
			fillCore(chunk);
			break;
		case AIR:
			fillAir(chunk);
			break;
		}
	}

	private void fillSurface(ChunkData chunk) {
		final int bpc = ChunkData.BLOCKS_PER_CHUNK;
		
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData granite = BlockDataRegistry.getInstance().get("Test:GraniteMonolith");
		
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		
		chunk.forEachBiC(bic -> {
			
			BlockData block;
			
			if (bic.z > bpc - 4) {
				block = dirt;
			} else {
				block = granite;
			}
			
			chunk.setBlockRel(bic, block, false);
			
		});
		
		VectorUtil.iterateCuboid(0, 0, bpc - 1, bpc, bpc, bpc, bic -> {
			chunk.getTilesRel(bic, RelFace.UP).add(grass);
		});
	}

	private void fillUndeground(ChunkData chunk) {
		fill(chunk, BlockDataRegistry.getInstance().get("Test:GraniteMonolith"));
	}

	private void fillEdgeSurface(ChunkData chunk) {
		fill(chunk, BlockDataRegistry.getInstance().get("Test:Stone"));
	}

	private void fillEdgeUnderground(ChunkData chunk) {
		fill(chunk, BlockDataRegistry.getInstance().get("Test:Stone"));
	}

	private void fillCore(ChunkData chunk) {
		fill(chunk, BlockDataRegistry.getInstance().get("Test:Stone"));
	}

	private void fillAir(ChunkData chunk) {
		fill(chunk, BlockDataRegistry.getInstance().get("Test:Air"));
	}
	
	private void fill(ChunkData chunk, BlockData block) {
		chunk.forEachBiC(bic -> chunk.setBlock(bic, block, false));
	}

	private ChunkType getChunkType(Vec3i pos) {
		int[] abs = pos.abs_().toIA_();
		Arrays.sort(abs);
		
		int medium = abs[1];
		int largest = abs[2];
		
		int level = largest;
		
		if (level == 0) {
			return ChunkType.CORE;
		}
		
		if (largest > surfaceLevel) {
			return ChunkType.AIR;
		}
		
		boolean isSurface = largest == surfaceLevel;
		
		if (medium == largest) {
			return isSurface ? ChunkType.EDGE_SURFACE : ChunkType.EDGE_UNDERGROUND;
		} else {
			return isSurface ? ChunkType.SURFACE : ChunkType.UNDERGROUND;
		}
	}

}
