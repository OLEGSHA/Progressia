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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.HashableVec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.io.ChunkIO;
import ru.windcorp.progressia.server.Server;

public class TestWorldDiskIO {

	private static Path SAVE_DIR = Paths.get("tmp_world");
	private static final String formatFile = "world.format";
	private static final Logger LOG = LogManager.getLogger("TestWorldDiskIO");
	
	private static HashMap<HashableVec3i, RandomAccessFile> randomAccessMap;
	private static final boolean ENABLE = true;

	private static int maxSize = 1048576;
	private static int sectorSize = maxSize / 256;

	private static final int bestFormat = 65536;

	// private Map<Vec3i,Vec3i> regions = new HashMap<Vec3i,Vec3i>();
	private static Vec3i regionSize;
	private static int chunksPerRegion;
	private static int offsetBytes;

	private static int currentFormat = -1;
	private static String extension = ".null";

	private static int natFromInt(int loc) {
		if (loc < 0)
			return (-loc) << 1 + 1;
		return loc << 1;
	}

	/*
	 * private static int intFromNat(int loc) // Possibly unused
	 * {
	 * if ((loc & 1) == 1)
	 * return -loc >> 1;
	 * return loc >> 1;
	 * }
	 */

	private static Vec3i getRegion(Vec3i chunkLoc) {
		return new Vec3i(
			natFromInt(chunkLoc.x / regionSize.x),
			natFromInt(chunkLoc.y / regionSize.y),
			natFromInt(chunkLoc.z / regionSize.z)
		);
	}

	private static int mod(int a, int m) {
		return ((a % m) + m) % m;
	}

	private static Vec3i getRegionLoc(Vec3i chunkLoc) {
		return new Vec3i(mod(chunkLoc.x, regionSize.x), mod(chunkLoc.y, regionSize.y), mod(chunkLoc.z, regionSize.z));
	}

	public static void initRegions() {
		initRegions(null);
	}

	public static void initRegions(Path worldPath) {
		if (worldPath != null) {
			SAVE_DIR = worldPath;
		}

		// regions.put(new Vec3i(0,0,0), new Vec3i(1,1,1));
	}

	private static void setRegionSize(int format) {
		randomAccessMap = new HashMap<HashableVec3i, RandomAccessFile>();
		switch (format) {
		case 0:
		case 1:
			regionSize = new Vec3i(1);
			chunksPerRegion = 1;
			currentFormat = format;
			extension = ".progressia_chunk";
			break;
		case 65536:
			regionSize = new Vec3i(16);
			chunksPerRegion = 16 * 16 * 16;
			currentFormat = 65536;
			offsetBytes = 3;
			extension = ".progressia_region";
			break;
		}
	}

	/*private static void expand(int sectors) {

	}*/

	public static void saveChunk(ChunkData chunk, Server server) {
		if (!ENABLE)
			return;

		try {

			if (currentFormat == 0) {
				LOG.debug(
					"Saving {} {} {}",
					chunk.getPosition().x,
					chunk.getPosition().y,
					chunk.getPosition().z
				);

				Files.createDirectories(SAVE_DIR);

				Path path = SAVE_DIR.resolve(
					String.format(
						"chunk_%+d_%+d_%+d" + extension,
						chunk.getPosition().x,
						chunk.getPosition().y,
						chunk.getPosition().z
					)
				);

				try (
					DataOutputStream output = new DataOutputStream(
						new DeflaterOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))
					)
				) {
					ChunkIO.save(chunk, output, IOContext.SAVE);
					writeGenerationHint(chunk, output, server);
				}
			} else if (currentFormat == 1) {
				LOG.debug(
					"Saving {} {} {}",
					chunk.getPosition().x,
					chunk.getPosition().y,
					chunk.getPosition().z
				);

				Files.createDirectories(SAVE_DIR);

				Vec3i saveCoords = getRegion(chunk.getPosition());

				Path path = SAVE_DIR.resolve(
					String.format(
						"chunk_%d_%d_%d" + extension,
						saveCoords.x,
						saveCoords.y,
						saveCoords.z
					)
				);

				try (
					DataOutputStream output = new DataOutputStream(
						new DeflaterOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))
					)
				) {
					ChunkIO.save(chunk, output, IOContext.SAVE);
					writeGenerationHint(chunk, output, server);
				}
			} else if (currentFormat == 65536) {
				LOG.debug(
					"Saving {} {} {}",
					chunk.getPosition().x,
					chunk.getPosition().y,
					chunk.getPosition().z
				);

				Files.createDirectories(SAVE_DIR);

				Vec3i saveCoords = getRegion(chunk.getPosition());

				Path path = SAVE_DIR.resolve(
					String.format(
						"%d_%d_%d" + extension,
						saveCoords.x,
						saveCoords.y,
						saveCoords.z
					)
				);

				/*
				 * if (!dosave)
				 * {
				 * return;
				 * }
				 * dosave = false;
				 */

				
					RandomAccessFile output = randomAccessMap.get(new HashableVec3i(saveCoords));
					if (output == null)
					{
						output = new RandomAccessFile(path.toFile(), "rw");
						randomAccessMap.put(new HashableVec3i(saveCoords), output);
					}
					// LOG.debug(output.read());
					if (output.read() < 0) {
						LOG.info("Making header");
						output.writeChars("\0".repeat((offsetBytes + 1) * chunksPerRegion));
					}

					Vec3i pos = getRegionLoc(chunk.getPosition());
					int shortOffset = (offsetBytes + 1) * (pos.z + regionSize.z * (pos.y + regionSize.y * pos.x));
					int fullOffset = (offsetBytes + 1) * (chunksPerRegion);
					output.seek(shortOffset);
					int offset = output.readInt();
					int sectorLength = offset & 255;
					offset = offset >> 8;
					if (sectorLength == 0) {
						int outputLen = (int) output.length();
						offset = (int) (outputLen - fullOffset) / sectorSize + 1;
						output.seek(shortOffset);
						output.writeInt(offset << 8);
						output.seek(outputLen);
						/*
						 * while (output.length()<fullOffset+sectorSize*offset)
						 * {
						 * output.write((int) (output.length()%256));
						 * }
						 */
						output.setLength(fullOffset + offset * sectorSize);
						// output.write(200);
					}

					// int bytestoWrite = output.readInt();
					// output.mark(sectorSize*sectorLength);

					// BufferedOutputStream counter = new
					// BufferedOutputStream(Files.newOutputStream(
					// SAVE_DIR.resolve(tempFile)));
					ByteArrayOutputStream tempDataStream = new ByteArrayOutputStream();
					DataOutputStream trueOutput = new DataOutputStream(
						new DeflaterOutputStream(
							new BufferedOutputStream(tempDataStream)
						)
					);
					// CountingOutputStream countOutput = new
					// CountingOutputStream(trueOutput);

					// LOG.info("Before: {}",output.);
					// trueOutput.writeBytes("uh try this");
					// counter.
					ChunkIO.save(chunk, trueOutput, IOContext.SAVE);
					writeGenerationHint(chunk, trueOutput, server);

					/*
					 * while (counter.getCount()%sectorSize != 0) {
					 * counter.write(0);
					 * }
					 */

					// LOG.info("Wrote {} bytes to
					// {},{},{}",trueOutput.size(),chunk.getPosition().x,chunk.getPosition().y,chunk.getPosition().z);

					trueOutput.close();

					byte tempData[] = tempDataStream.toByteArray();

					output.seek((long) fullOffset + sectorSize * offset);
					output.write(tempData);

					output.seek(shortOffset + offsetBytes);
					output.write((int) tempData.length / sectorSize + 1);
					// LOG.info("Used {} sectors",(int)
					// tempData.length/sectorSize + 1);

			}
			// else if (currentFormat)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeGenerationHint(ChunkData chunk, DataOutputStream output, Server server)
		throws IOException {
		server.getWorld().getGenerator().writeGenerationHint(output, chunk.getGenerationHint());
	}

	public static ChunkData tryToLoad(Vec3i chunkPos, WorldData world, Server server) {
		if (!ENABLE)
			return null;

		if (currentFormat == -1) {
			Path formatPath = SAVE_DIR.resolve(formatFile);
			File format = formatPath.toFile();

			if (format.exists()) {
				String data = null;
				try {
					Scanner reader = new Scanner(format);

					data = reader.next();

					reader.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				byte[] formatBytes = data.getBytes();
				int formatNum = formatBytes[0] * 256 * 256 * 256 + formatBytes[1] * 256 * 256 + formatBytes[2] * 256
					+ formatBytes[3];

				setRegionSize(formatNum);
			} else {
				setRegionSize(bestFormat);

				LOG.debug("Making new world with format {}", bestFormat);

				BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(format));

					int bfClone = bestFormat;

					for (int i = 0; i < 4; i++) {
						bw.write(bfClone >> 24);
						LOG.debug(bfClone >> 24);
						bfClone = bfClone << 8;
					}

					/*
					 * bw.write(
					 * new char[] {
					 * (char) bestFormat / (256 * 256 * 256),
					 * (char) (bestFormat % 256) / (256 * 256),
					 * (char) (bestFormat % (256 * 256)) / (256),
					 * (char) (bestFormat % (256 * 256 * 256)) }
					 * );
					 */

					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		if (currentFormat == 0) {

			Path path = SAVE_DIR.resolve(
				String.format(
					"chunk_%+d_%+d_%+d" + extension,
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				)
			);

			if (!Files.exists(path)) {
				LOG.debug(
					"Not found {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);

				return null;
			}

			try {
				ChunkData result = load(path, chunkPos, world, server);

				LOG.debug(
					"Loaded {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);

				return result;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.debug(
					"Could not load {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);
				return null;
			}
		} else if (currentFormat == 1) {
			Vec3i saveCoords = getRegion(chunkPos);

			Path path = SAVE_DIR.resolve(
				String.format(
					"chunk_%d_%d_%d" + extension,
					saveCoords.x,
					saveCoords.y,
					saveCoords.z
				)
			);

			if (!Files.exists(path)) {
				LOG.debug(
					"Not found {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);

				return null;
			}

			try {
				ChunkData result = load(path, chunkPos, world, server);

				LOG.debug(
					"Loaded {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);

				return result;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.debug(
					"Could not load {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);
				return null;
			}
		} else if (currentFormat == 65536) {
			Vec3i saveCoords = getRegion(chunkPos);

			Path path = SAVE_DIR.resolve(
				String.format(
					"%d_%d_%d" + extension,
					saveCoords.x,
					saveCoords.y,
					saveCoords.z
				)
			);

			if (!Files.exists(path)) {
				LOG.debug(
					"Not found {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);

				return null;
			}

			try {
				ChunkData result = loadRegion(path, chunkPos, world, server);

				LOG.debug(
					"Loaded {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);

				return result;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.debug(
					"Could not load {} {} {}",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				);
				return null;
			}
		}
		return null;
	}

	private static ChunkData load(Path path, Vec3i chunkPos, WorldData world, Server server)
		throws IOException,
		DecodingException {
		try (
			DataInputStream input = new DataInputStream(
				new InflaterInputStream(new BufferedInputStream(Files.newInputStream(path)))
			)
		) {
			ChunkData chunk = ChunkIO.load(world, chunkPos, input, IOContext.SAVE);
			readGenerationHint(chunk, input, server);
			return chunk;
		}
	}

	private static ChunkData loadRegion(Path path, Vec3i chunkPos, WorldData world, Server server)
		throws IOException,
		DecodingException {
			Vec3i streamCoords = getRegion(chunkPos);
			
			RandomAccessFile input = randomAccessMap.get(new HashableVec3i(streamCoords));
			if (input == null)
			{
				input = new RandomAccessFile(path.toFile(), "rw");
				randomAccessMap.put(new HashableVec3i(streamCoords), input);
			}

			// LOG.info(path.toString());
			Vec3i pos = getRegionLoc(chunkPos);
			
			int shortOffset = (offsetBytes + 1) * (pos.z + regionSize.z * (pos.y + regionSize.y * pos.x));
			int fullOffset = (offsetBytes + 1) * (chunksPerRegion);
			input.seek(shortOffset);
			int offset = 0;
			for (int i = 0; i < offsetBytes; i++) {
				offset *= 256;
				offset += input.read();
			}
			int sectorLength = input.read();
			input.seek(fullOffset + sectorSize * offset);

			// LOG.info("Read {} sectors", sectorLength);

			byte tempData[] = new byte[sectorSize * sectorLength];
			input.read(tempData);

			DataInputStream trueInput = new DataInputStream(
				new InflaterInputStream(new BufferedInputStream(new ByteArrayInputStream(tempData)))
			);
			ChunkData chunk = ChunkIO.load(world, chunkPos, trueInput, IOContext.SAVE);
			readGenerationHint(chunk, trueInput, server);
			return chunk;
	}

	private static void readGenerationHint(ChunkData chunk, DataInputStream input, Server server)
		throws IOException,
		DecodingException {
		chunk.setGenerationHint(server.getWorld().getGenerator().readGenerationHint(input));
	}

}
