package ru.windcorp.progressia.test.gen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.generation.AbstractWorldGenerator;

public class TestWorldGenerator extends AbstractWorldGenerator<Boolean> {
	
	public TestWorldGenerator(WorldLogic world) {
		super("Test:WorldGenerator", Boolean.class);
		
		world.getData().addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(WorldData world, ChunkData chunk) {
				findAndPopulate(chunk.getPosition(), world);
			}
		});
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
		ChunkData chunk = generateUnpopulated(chunkPos, world);
		world.addChunk(chunk);
		return chunk;
	}

	private ChunkData generateUnpopulated(Vec3i chunkPos, WorldData world) {
		ChunkData chunk = new ChunkData(chunkPos, world);
		chunk.setGenerationHint(false);
		
		final int bpc = ChunkData.BLOCKS_PER_CHUNK;
		
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		
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
		
		VectorUtil.iterateCuboid(0, 0, 0, bpc, bpc, bpc, pos -> {
			int layer = pos.z - heightMap[pos.x][pos.y];
			
			if (layer < -4) {
				chunk.setBlock(pos, stone, false);
			} else if (layer < 0) {
				chunk.setBlock(pos, dirt, false);
			} else {
				chunk.setBlock(pos, air, false);
			}
		});
		
		return chunk;
	}

	private void findAndPopulate(Vec3i changePos, WorldData world) {
		VectorUtil.iterateCuboidAround(changePos, 3, candidatePos -> {
			if (canBePopulated(candidatePos, world)) {
				populate(candidatePos, world);
			}
		});
	}

	private boolean canBePopulated(Vec3i candidatePos, WorldData world) {
		Vec3i cursor = Vectors.grab3i();
		
		ChunkData candidate = world.getChunk(candidatePos);
		if (candidate == null || isChunkReady(candidate.getGenerationHint())) return false;
		
		for (int dx = -1; dx <= 1; ++dx) {
			cursor.x = candidatePos.x + dx;
			for (int dy = -1; dy <= 1; ++dy) {
				cursor.y = candidatePos.y + dy;
				for (int dz = -1; dz <= 1; ++dz) {
					
					if ((dx | dy | dz) == 0) continue;
					
					cursor.z = candidatePos.z + dz;
					
					ChunkData chunk = world.getChunk(cursor);
					if (chunk == null) {
						return false;
					}
					
				}
			}
		}
		
		Vectors.release(cursor);
		return true;
	}
	
	private void populate(Vec3i chunkPos, WorldData world) {
		Random random = new Random(chunkPos.x + chunkPos.y + chunkPos.z);
		
		ChunkData chunk = world.getChunk(chunkPos);
		assert chunk != null : "Something went wrong when populating chunk at (" + chunkPos.x + "; " + chunkPos.y + "; " + chunkPos.z + ")";
		
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		
		Vec3i biw = new Vec3i();
		
		int minX = Coordinates.getInWorld(chunkPos.x, 0);
		int maxX = Coordinates.getInWorld(chunkPos.x + 1, 0);
		int minY = Coordinates.getInWorld(chunkPos.y, 0);
		int maxY = Coordinates.getInWorld(chunkPos.y + 1, 0);
		int minZ = Coordinates.getInWorld(chunkPos.z, 0);
		int maxZ = Coordinates.getInWorld(chunkPos.z + 1, 0);
		
		for (biw.x = minX; biw.x < maxX; ++biw.x) {
			for (biw.y = minY; biw.y < maxY; ++biw.y) {
				
				for (biw.z = minZ; biw.z < maxZ + 1 && world.getBlock(biw) != air; ++biw.z);
				biw.z -= 1;
				
				if (biw.z == maxZ) continue;
				if (biw.z < minZ) continue;
				
				addTiles(chunk, biw, world, random);
				
			}
		}

		chunk.setGenerationHint(true);
	}
	
	private void addTiles(ChunkData chunk, Vec3i biw, WorldData world, Random random) {
		addGrass(chunk, biw, world, random);
		addDecor(chunk, biw, world, random);
	}

	private void addGrass(ChunkData chunk, Vec3i biw, WorldData world, Random random) {
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		
		world.getTiles(biw, BlockFace.TOP).add(grass);
		
		for (BlockFace face : BlockFace.getFaces()) {
			if (face.getVector().z != 0) continue;
			biw.add(face.getVector());
			
			if (world.getBlock(biw) == air) {
				biw.sub(face.getVector());
				world.getTiles(biw, face).add(grass);
			} else {
				biw.sub(face.getVector());
			}
		}
	}

	private void addDecor(ChunkData chunk, Vec3i biw, WorldData world, Random random) {
		if (random.nextInt(8) == 0) {
			world.getTiles(biw, BlockFace.TOP).addFarthest(
					TileDataRegistry.getInstance().get("Test:Sand")
			);
		}
		
		if (random.nextInt(8) == 0) {
			world.getTiles(biw, BlockFace.TOP).addFarthest(
					TileDataRegistry.getInstance().get("Test:Stones")
			);
		}
		
		if (random.nextInt(8) == 0) {
			world.getTiles(biw, BlockFace.TOP).addFarthest(
					TileDataRegistry.getInstance().get("Test:YellowFlowers")
			);
		}
	}

}
