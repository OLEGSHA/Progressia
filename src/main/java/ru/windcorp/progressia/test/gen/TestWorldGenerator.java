package ru.windcorp.progressia.test.gen;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.generation.WorldGenerator;

public class TestWorldGenerator extends WorldGenerator {

	public TestWorldGenerator(WorldLogic world) {
		super("Test:WorldGenerator");
	}

	@Override
	public ChunkData generate(Vec3i chunkPos, WorldData world) {
		ChunkData chunk = new ChunkData(chunkPos, world);
		
		final int bpc = ChunkData.BLOCKS_PER_CHUNK;
		
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
	
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		TileData stones = TileDataRegistry.getInstance().get("Test:Stones");
		TileData flowers = TileDataRegistry.getInstance().get("Test:YellowFlowers");
		TileData sand = TileDataRegistry.getInstance().get("Test:Sand");
		
		final float maxHeight = 32;
		final float rho = 2000;
		
		int[][] heightMap = new int[bpc][bpc];
		
		for (int yic = 0; yic < heightMap.length; ++yic) {
			int yiw = Coordinates.getInWorld(chunk.getY(), yic);
			for (int xic = 0; xic < heightMap[yic].length; ++xic) {
				int xiw = Coordinates.getInWorld(chunk.getX(), xic);
				
				int rsq = (xiw*xiw + yiw*yiw);
				heightMap[xic][yic] = (int) (rsq / (rho + rsq) * maxHeight) - chunk.getZ()*bpc;
			}
		}
		
		Vec3i pos = new Vec3i();
		
		for (pos.x = 0; pos.x < bpc; ++pos.x) {
			for (pos.y = 0; pos.y < bpc; ++pos.y) {
				for (pos.z = 0; pos.z < bpc; ++pos.z) {

					int layer = pos.z - heightMap[pos.x][pos.y];
					
					if (layer < -4) {
						chunk.setBlock(pos, stone, false);
					} else if (layer < 0) {
						chunk.setBlock(pos, dirt, false);
					} else {
						chunk.setBlock(pos, air, false);
					}
					
				}
			}
		}
		
		for (int x = 0; x < bpc; ++x) {
			for (int y = 0; y < bpc; ++y) {
				
//				int z = heightMap[x][y];
				
				for (int z = 0; z < bpc; ++z) {

					pos.set(x, y, z);
					int layer = pos.z - heightMap[x][y];
					
					if (layer == -1) {
						chunk.getTiles(pos, BlockFace.TOP).add(grass);
						for (BlockFace face : BlockFace.getFaces()) {
							if (face.getVector().z != 0) continue;
							pos.add(face.getVector());
							
							if (!ChunkData.isInBounds(pos) || (chunk.getBlock(pos) == air)) {
								pos.sub(face.getVector());
								chunk.getTiles(pos, face).add(grass);
							} else {
								pos.sub(face.getVector());
							}
						}
						
						int hash = x*x * 19 ^ y*y * 41 ^ pos.z*pos.z * 147;
						if (hash % 5 == 0) {
							chunk.getTiles(pos, BlockFace.TOP).addFarthest(sand);
						}
						
						hash = x*x * 13 ^ y*y * 37 ^ pos.z*pos.z * 129;
						if (hash % 5 == 0) {
							chunk.getTiles(pos, BlockFace.TOP).addFarthest(stones);
						}
						
						hash = x*x * 17 ^ y*y * 39 ^ pos.z*pos.z * 131;
						if (hash % 9 == 0) {
							chunk.getTiles(pos, BlockFace.TOP).addFarthest(flowers);
						}
					}
				}
			}
		}
		
		return chunk;
	}

}
