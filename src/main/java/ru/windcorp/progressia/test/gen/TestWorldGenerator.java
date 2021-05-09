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

package ru.windcorp.progressia.test.gen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListener;
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
import ru.windcorp.progressia.test.TestEntityDataFallingBlock;
import ru.windcorp.progressia.test.TestEntityLogicFallingBlock;

public class TestWorldGenerator extends AbstractWorldGenerator<Boolean> {

	private final TestTerrainGenerator terrainGen;

	public TestWorldGenerator(WorldLogic world) {
		super("Test:WorldGenerator", Boolean.class);
		this.terrainGen = new TestTerrainGenerator(this, world);

		world.getData().addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(WorldData world, ChunkData chunk) {
				findAndPopulate(chunk.getPosition(), world);
				chunk.addListener(new ChunkDataListener() {
					@Override
					public void onChunkBlockChanged(ChunkData chunk, Vec3i blockInChunk, BlockData previous,
							BlockData current) {
						if (!TestEntityLogicFallingBlock.FallingBlocks.contains(current.getId())) {
							return;
						}
						if (chunk.getWorld().getBlock(chunk.getPosition().mul_(16).add_(blockInChunk.add_(0, 0, -1))).getId() == "Test:Air")
						{
							LogManager.getLogger().info("Inserting FallingBlock");

							TestEntityDataFallingBlock fallingBlock = new TestEntityDataFallingBlock();

							Vec3i worldPos = chunk.getPosition().mul_(16).add_(blockInChunk);
							Vec3 floatWorldPos = new Vec3(worldPos.x, worldPos.y, worldPos.z);
							fallingBlock.setPosition(floatWorldPos);

							fallingBlock.setEntityId(("Test:FallingBlock" + floatWorldPos.toString()
									+ String.valueOf(new Random().nextFloat())).hashCode());

							chunk.getWorld().addEntity(fallingBlock);
							chunk.setBlock(blockInChunk, previous, true);
							Vec3i chunkWorldPos = chunk.getPosition().mul_(16).add_(blockInChunk);
							LogManager.getLogger().info(String.valueOf(chunkWorldPos.x)+" "+String.valueOf(chunkWorldPos.y)+" "+String.valueOf(chunkWorldPos.z));
							ClientState.getInstance().getWorld().getData().setBlock(chunkWorldPos, BlockDataRegistry.getInstance().get("Test:Glass"), true);
						}
					}
				});
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
		Random random = new Random(chunkPos.x + chunkPos.y + chunkPos.z);

		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

		BlockData[] granites = new BlockData[] { BlockDataRegistry.getInstance().get("Test:GraniteGravel"),
				BlockDataRegistry.getInstance().get("Test:GraniteGravel"),
				BlockDataRegistry.getInstance().get("Test:GraniteCracked"),
				BlockDataRegistry.getInstance().get("Test:GraniteMonolith") };

		double[][] heightMap = new double[bpc][bpc];
		double[][] gradMap = new double[bpc][bpc];

		int startX = Coordinates.getInWorld(chunk.getX(), 0);
		int startY = Coordinates.getInWorld(chunk.getY(), 0);
		int startZ = Coordinates.getInWorld(chunk.getZ(), 0);

		terrainGen.compute(startX, startY, heightMap, gradMap);

		VectorUtil.iterateCuboid(0, 0, 0, bpc, bpc, bpc, pos -> {
			double layer = pos.z - heightMap[pos.x][pos.y] + startZ;

			if (layer < -4) {
				chunk.setBlock(pos, stone, false);
			} else if (layer < 0) {
				if (gradMap[pos.x][pos.y] > 0.5) {
					BlockData granite = granites[random.nextInt(4)];
					chunk.setBlock(pos, granite, false);
				} else {
					chunk.setBlock(pos, dirt, false);
				}
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
		if (candidate == null || isChunkReady(candidate.getGenerationHint()))
			return false;

		for (int dx = -1; dx <= 1; ++dx) {
			cursor.x = candidatePos.x + dx;
			for (int dy = -1; dy <= 1; ++dy) {
				cursor.y = candidatePos.y + dy;
				for (int dz = -1; dz <= 1; ++dz) {

					if ((dx | dy | dz) == 0)
						continue;

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
		assert chunk != null : "Something went wrong when populating chunk at (" + chunkPos.x + "; " + chunkPos.y + "; "
				+ chunkPos.z + ")";

		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");

		Vec3i biw = new Vec3i();

		int minX = chunk.getMinX();
		int maxX = chunk.getMaxX() + 1;
		int minY = chunk.getMinY();
		int maxY = chunk.getMaxY() + 1;
		int minZ = chunk.getMinZ();
		int maxZ = chunk.getMaxZ() + 1;

		final int bpc = ChunkData.BLOCKS_PER_CHUNK;
		double[][] heightMap = new double[bpc][bpc];
		double[][] gradMap = new double[bpc][bpc];

		terrainGen.compute(minX, minY, heightMap, gradMap);

		for (biw.x = minX; biw.x < maxX; ++biw.x) {
			for (biw.y = minY; biw.y < maxY; ++biw.y) {

				for (biw.z = minZ; biw.z < maxZ + 1 && world.getBlock(biw) != air; ++biw.z)
					;
				biw.z -= 1;

				if (biw.z == maxZ)
					continue;
				if (biw.z < minZ)
					continue;

				int xic = Coordinates.convertInWorldToInChunk(biw.x);
				int yic = Coordinates.convertInWorldToInChunk(biw.y);

				addTiles(chunk, biw, world, random, world.getBlock(biw) == dirt, heightMap[xic][yic],
						gradMap[xic][yic]);

			}
		}

		chunk.setGenerationHint(true);
	}

	private void addTiles(ChunkData chunk, Vec3i biw, WorldData world, Random random, boolean isDirt, double height,
			double grad) {
		if (isDirt)
			addGrass(chunk, biw, world, random);
		addDecor(chunk, biw, world, random, isDirt);
		addSnow(chunk, biw, world, random, isDirt, height, grad);
	}

	private void addGrass(ChunkData chunk, Vec3i biw, WorldData world, Random random) {
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");

		world.getTiles(biw, BlockFace.TOP).add(grass);

		for (BlockFace face : BlockFace.getFaces()) {
			if (face.getVector().z != 0)
				continue;
			biw.add(face.getVector());

			if (world.getBlock(biw) == air) {
				biw.sub(face.getVector());
				world.getTiles(biw, face).add(grass);
			} else {
				biw.sub(face.getVector());
			}
		}
	}

	private void addDecor(ChunkData chunk, Vec3i biw, WorldData world, Random random, boolean isDirt) {
		if (isDirt) {
			if (random.nextInt(8) == 0) {
				world.getTiles(biw, BlockFace.TOP).addFarthest(TileDataRegistry.getInstance().get("Test:Sand"));
			}

			if (random.nextInt(8) == 0) {
				world.getTiles(biw, BlockFace.TOP).addFarthest(TileDataRegistry.getInstance().get("Test:Stones"));
			}

			if (random.nextInt(8) == 0) {
				world.getTiles(biw, BlockFace.TOP)
						.addFarthest(TileDataRegistry.getInstance().get("Test:YellowFlowers"));
			}
		} else {
			if (random.nextInt(2) == 0) {
				world.getTiles(biw, BlockFace.TOP).addFarthest(TileDataRegistry.getInstance().get("Test:Stones"));
			}
		}
	}

	private void addSnow(ChunkData chunk, Vec3i biw, WorldData world, Random random, boolean isDirt, double height,
			double grad) {
		if (height < 1500)
			return;

		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

		double quarterChance = computeSnowQuarterChance(height, grad);
		double halfChance = computeSnowHalfChance(height, grad);
		double opaqueChance = computeSnowOpaqueChance(height, grad);

		for (BlockFace face : BlockFace.getFaces()) {
			if (face == BlockFace.BOTTOM)
				continue;

			if (face.getVector().z == 0) {
				biw.add(face.getVector());
				BlockData neighbour = world.getBlock(biw);
				biw.sub(face.getVector());

				if (neighbour != air)
					continue;
			}

			TileData tile;

			double maxValue = height > 3000 ? 3 : (1 + 2 * ((height - 1500) / 1500));
			double value = random.nextDouble() * maxValue;
			if (value < quarterChance) {
				tile = TileDataRegistry.getInstance().get("Test:SnowQuarter");
			} else if ((value -= quarterChance) < halfChance) {
				tile = TileDataRegistry.getInstance().get("Test:SnowHalf");
			} else if ((value -= halfChance) < opaqueChance) {
				tile = TileDataRegistry.getInstance().get("Test:SnowOpaque");
			} else {
				tile = null;
			}

			if (tile != null) {
				world.getTiles(biw, face).addFarthest(tile);
			}
		}
	}

	private double computeSnowQuarterChance(double height, double grad) {
		double heightCoeff;

		if (height < 1500)
			heightCoeff = 0;
		else if (height < 2000)
			heightCoeff = (height - 1500) / 500;
		else
			heightCoeff = 1;

		if (heightCoeff < 1e-4)
			return 0;

		double gradCoeff = computeSnowGradCoeff(height, grad);
		return heightCoeff * gradCoeff;
	}

	private double computeSnowHalfChance(double height, double grad) {
		double heightCoeff;

		if (height < 2000)
			heightCoeff = 0;
		else if (height < 2500)
			heightCoeff = (height - 2000) / 500;
		else
			heightCoeff = 1;

		if (heightCoeff < 1e-4)
			return 0;

		double gradCoeff = computeSnowGradCoeff(height, grad);
		return heightCoeff * gradCoeff;
	}

	private double computeSnowOpaqueChance(double height, double grad) {
		double heightCoeff;

		if (height < 2500)
			heightCoeff = 0;
		else if (height < 3000)
			heightCoeff = (height - 2500) / 500;
		else
			heightCoeff = 1;

		if (heightCoeff < 1e-4)
			return 0;

		double gradCoeff = computeSnowGradCoeff(height, grad);
		return heightCoeff * gradCoeff;
	}

	private double computeSnowGradCoeff(double height, double grad) {
		final double a = -0.00466666666666667;
		final double b = 12.66666666666667;
		double characteristicGrad = 1 / (a * height + b);
		return Math.exp(-grad / characteristicGrad);
	}

}
