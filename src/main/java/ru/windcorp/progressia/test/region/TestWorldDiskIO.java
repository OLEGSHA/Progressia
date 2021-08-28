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

package ru.windcorp.progressia.test.region;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.HashableVec3i;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.io.ChunkIO;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.io.WorldContainer;

public class TestWorldDiskIO implements WorldContainer {
	
	private static final boolean resetCorrupted = true;
	
	public class RandomFileMapped {
		public RandomAccessFile file;
		public HashMap<HashableVec3i, Integer> offsets;
		public HashMap<HashableVec3i, Integer> lengths;
		
		public RandomFileMapped(RandomAccessFile inFile)
		{
			boolean check = false;
			offsets = new HashMap<>();
			lengths = new HashMap<>();
			try {
				check = confirmHeaderHealth(inFile, offsets, lengths);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!check)
			{
				LOG.debug("Uh the file broke");
				if (resetCorrupted) {
					byte headerBytes[] = new byte[4 * chunksPerRegion];
					for (int i = 0; i < 4 * chunksPerRegion; i++) {
						headerBytes[i] = (byte) 0;
					}
					try {
						inFile.write(headerBytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			file = inFile;
		}
		
		public int getOffset(Vec3i chunkLoc)
		{
			return offsets.get(new HashableVec3i(chunkLoc));
		}

		public boolean hasOffset(Vec3i pos) {
			return offsets.containsKey(new HashableVec3i(pos));
		}
		
		public void putOffset(Vec3i pos, int offset)
		{
			offsets.put(new HashableVec3i(pos), offset);
		}
		
		public int getLength(Vec3i chunkLoc)
		{
			return lengths.get(new HashableVec3i(chunkLoc));
		}

		public boolean hasLength(Vec3i pos) {
			return lengths.containsKey(new HashableVec3i(pos));
		}
		
		public void putLength(Vec3i pos, int length)
		{
			lengths.put(new HashableVec3i(pos), length);
		}
	}

	private Path SAVE_DIR = Paths.get("tmp_world");
	private static final String formatFile = "world.format";
	private static final Logger LOG = LogManager.getLogger("TestWorldDiskIO");
	
	private HashMap<HashableVec3i, RandomFileMapped> inOutMap;
	private static final boolean ENABLE = false;

	private static int maxSize = 1048576;
	private static int sectorSize = maxSize / 256;

	private static final int bestFormat = 65536;

	// private Map<Vec3i,Vec3i> regions = new HashMap<Vec3i,Vec3i>();
	private Vec3i regionSize;
	private int chunksPerRegion;

	private int currentFormat = -1;
	private String extension = ".null";

	private static int natFromInt(int loc) {
		if (loc < 0)
			return -2*loc - 1;
		return 2*loc;
	}

	/*
	 * private static int intFromNat(int loc) // Possibly unused
	 * {
	 * if ((loc & 1) == 1)
	 * return -loc >> 1;
	 * return loc >> 1;
	 * }
	 */

	private Vec3i getRegion(Vec3i chunkLoc) {
		int x = chunkLoc.x;
		if (x<0)
		{
			x /= regionSize.x;
			x--;
		}
		else
		{
			x /= regionSize.x;
		}
		int y = chunkLoc.y;
		if (y<0)
		{
			y /= regionSize.y;
			y--;
		}
		else
		{
			y /= regionSize.y;
		}
		int z = chunkLoc.z;
		if (z<0)
		{
			z /= regionSize.z;
			z--;
		}
		else
		{
			z /= regionSize.z;
		}
		return new Vec3i(
			natFromInt(x),
			natFromInt(y),
			natFromInt(z)
		);
	}

	private static int mod(int a, int m) {
		return ((a % m) + m) % m;
	}

	private Vec3i getRegionLoc(Vec3i chunkLoc) {
		return new Vec3i(mod(chunkLoc.x, regionSize.x), mod(chunkLoc.y, regionSize.y), mod(chunkLoc.z, regionSize.z));
	}

	public TestWorldDiskIO(Path worldPath) {
		if (worldPath != null) {
			SAVE_DIR = worldPath;
		}

		// regions.put(new Vec3i(0,0,0), new Vec3i(1,1,1));
	}
	
	/*public static int getAvailableSector(MappedByteBuffer mbb)
	{
		int sectorsUsed = 0;
		for (int i=offsetBytes; i<(offsetBytes+1)*chunksPerRegion; i+= (offsetBytes+1))
		{
			
			sectorsUsed += mbb.get(i);
		}
		return sectorsUsed;
	}*/

	private void setRegionSize(int format) {
		inOutMap = new HashMap<HashableVec3i, RandomFileMapped>();
		switch (format) {
		case 65536:
		default:
			regionSize = new Vec3i(16);
			chunksPerRegion = 16 * 16 * 16;
			currentFormat = 65536;
			extension = ".progressia_region";
			break;
		case 65537:
			regionSize = new Vec3i(16);
			chunksPerRegion = 16 * 16 * 16;
			currentFormat = 65536;
			extension = ".progressia_regionx";
			break;
		}
	}

	public boolean confirmHeaderHealth(RandomAccessFile input, HashMap<HashableVec3i, Integer> offsets, HashMap<HashableVec3i, Integer> length) throws IOException
	{
		Set<Integer> used = new HashSet<Integer>();
		input.seek(0);
		if (input.length() < 4*chunksPerRegion)
		{
			return false;
		}
		for (int i=0;i<4*chunksPerRegion;i+=4)
		{
			int offset = 0;
			for (int ii = 0; ii < 3; ii++) {
				offset *= 256;
				offset += input.read();
			}
			int sectorLength = input.read();
			if (sectorLength==0)
			{
				continue;
			}
			int headerPos = i/4;
			int x = headerPos/regionSize.y/regionSize.z;
			int y = (headerPos/regionSize.z)%regionSize.y;
			int z = headerPos%regionSize.z;
			HashableVec3i key = new HashableVec3i(new Vec3i(x,y,z));
			offsets.put(key , offset);
			length.put(key, sectorLength);
			for (int ii=0;ii<sectorLength;ii++)
			{
				if (used.contains(offset+ii))
				{
					return false;
				}
				used.add(offset+ii);
			}
		}
		return true;
	}

	@Override
	public void save(DefaultChunkData chunk, DefaultWorldData world, Server server) {
		if (!ENABLE)
			return;

		try {

			if (currentFormat == 65536) {
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

				
					RandomFileMapped outputMap = inOutMap.get(new HashableVec3i(saveCoords));
					//LOG.info("saveCoords {},{},{}", saveCoords.x, saveCoords.y, saveCoords.z);
					if (outputMap == null)
					{
						outputMap = makeNew(path, new HashableVec3i(saveCoords));
					}
					RandomAccessFile output = outputMap.file;
					
					Vec3i pos = getRegionLoc(chunk.getPosition());
					int shortOffset = 4 * (pos.z + regionSize.z * (pos.y + regionSize.y * pos.x));
					int fullOffset = 4 * (chunksPerRegion);
					int offset = 0;
					
					if (outputMap.hasOffset(pos))
					{
						offset = outputMap.getOffset(pos);
					}
					else {
						output.seek(shortOffset);
						for (int i = 0; i < 3; i++) {
							offset *= 256;
							offset += output.read();
						}
						int sectorLength = output.read();
						if (sectorLength == 0) {
							int outputLen = (int) output.length();
							offset = (int) (outputLen - fullOffset) / sectorSize + 1;
							int tempOffset = offset;
							output.seek(shortOffset);

							byte readOffset[] = new byte[3];
							for (int i = 0; i < 3; i++) {
								readOffset[2 - i] = (byte) (tempOffset % 256);
								tempOffset >>= 8;
							}
							output.write(readOffset);

							output.setLength(fullOffset + offset * sectorSize);
						}
						outputMap.putOffset(pos, offset);
					}

					ByteArrayOutputStream tempDataStream = new ByteArrayOutputStream();
					DataOutputStream trueOutput = new DataOutputStream(
						new DeflaterOutputStream(
							new BufferedOutputStream(tempDataStream)
						)
					);
					ChunkIO.save(chunk, trueOutput, IOContext.SAVE);
					writeGenerationHint(chunk, trueOutput, server);
					
					trueOutput.close();

					byte tempData[] = tempDataStream.toByteArray();

					output.seek( fullOffset + sectorSize * offset);
					output.write(tempData);

					output.seek(shortOffset + 3);
					output.write(tempData.length / sectorSize + 1);
					outputMap.putLength(pos, tempData.length / sectorSize + 1);
					// LOG.info("Used {} sectors",(int)
					// tempData.length/sectorSize + 1);

			}
			else if (currentFormat == 65537) {
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

				
					RandomFileMapped outputMap = inOutMap.get(new HashableVec3i(saveCoords));
					//LOG.info("saveCoords {},{},{}", saveCoords.x, saveCoords.y, saveCoords.z);
					if (outputMap == null)
					{
						outputMap = makeNew(path, new HashableVec3i(saveCoords));
					}
					RandomAccessFile output = outputMap.file;
					
					Vec3i pos = getRegionLoc(chunk.getPosition());
					int shortOffset = 4 * (pos.z + regionSize.z * (pos.y + regionSize.y * pos.x));
					int fullOffset = 4 * (chunksPerRegion);
					int offset = 0;
					
					if (outputMap.hasOffset(pos))
					{
						offset = outputMap.getOffset(pos);
					}
					else {
						output.seek(shortOffset);
						for (int i = 0; i < 3; i++) {
							offset *= 256;
							offset += output.read();
						}
						int sectorLength = output.read();
						if (sectorLength == 0) {
							int outputLen = (int) output.length();
							offset = (int) (outputLen - fullOffset) / sectorSize + 1;
							int tempOffset = offset;
							output.seek(shortOffset);

							byte readOffset[] = new byte[3];
							for (int i = 0; i < 3; i++) {
								readOffset[2 - i] = (byte) (tempOffset % 256);
								tempOffset >>= 8;
							}
							output.write(readOffset);

							output.setLength(fullOffset + offset * sectorSize);
						}
						outputMap.putOffset(pos, offset);
					}

					ByteArrayOutputStream tempDataStream = new ByteArrayOutputStream();
					DataOutputStream trueOutput = new DataOutputStream(
						new DeflaterOutputStream(
							new BufferedOutputStream(tempDataStream)
						)
					);
					ChunkIO.save(chunk, trueOutput, IOContext.SAVE);
					writeGenerationHint(chunk, trueOutput, server);
					
					trueOutput.close();

					byte tempData[] = tempDataStream.toByteArray();

					output.seek( fullOffset + sectorSize * offset);
					
					chunk.computeOpaque();
					chunk.computeEmpty();
					output.write((chunk.isOpaque() ? 1 : 0) << 1 + (chunk.isEmpty() ? 1 : 0)); //Writes extra flag byte of whether or not the chunk is empty or solid
					output.write(tempData);

					output.seek(shortOffset + 3);
					output.write(tempData.length / sectorSize + 1);
					outputMap.putLength(pos, tempData.length / sectorSize + 1);
					// LOG.info("Used {} sectors",(int)
					// tempData.length/sectorSize + 1);

			}
			// else if (currentFormat)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private RandomFileMapped makeNew(Path path, Object hashObj) {
		try
		{
		RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
		//FileChannel fc = raf.getChannel();
		//MappedByteBuffer output = fc.map(FileChannel.MapMode.READ_WRITE, 0, maxSize*chunksPerRegion);
		//output.limit(maxSize*chunksPerRegion);
		RandomFileMapped rfm = new RandomFileMapped(raf);
		inOutMap.put((HashableVec3i) hashObj, rfm);
		return rfm;
		}
		catch (IOException e)
		{
			LOG.warn("bad things");
		}
		return null;
	}

	private void writeGenerationHint(DefaultChunkData chunk, DataOutputStream output, Server server)
		throws IOException {
		server.getWorld().getGenerator().writeGenerationHint(output, chunk.getGenerationHint());
	}

	@Override
	public DefaultChunkData load(Vec3i chunkPos, DefaultWorldData world, Server server) {
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

		if (currentFormat == 65536) {
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
				DefaultChunkData result = loadRegion(path, chunkPos, world, server);

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
		else if (currentFormat == 65537) {
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
				DefaultChunkData result = loadRegionX(path, chunkPos, world, server);

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

	private DefaultChunkData loadRegion(Path path, Vec3i chunkPos, DefaultWorldData world, Server server)
		throws IOException,
		DecodingException {
		int offset = 0;
		int sectorLength = 0;
		Vec3i pos;
		RandomFileMapped inputMap;
		int fullOffset = 4 * (chunksPerRegion);
		try
		{
			Vec3i streamCoords = getRegion(chunkPos);
			
			inputMap = inOutMap.get(new HashableVec3i(streamCoords));
			//LOG.info("streamCoords {},{},{}", streamCoords.x,streamCoords.y,streamCoords.z);
			if (inputMap == null)
			{
				//input = new RandomAccessFile(path.toFile(), "rw");
				//input = Files.newByteChannel(path);
				inputMap = makeNew(path, new HashableVec3i(streamCoords));
			}
			
			RandomAccessFile input = inputMap.file;
			
			
			pos = getRegionLoc(chunkPos);
			
			if (inputMap.hasOffset(pos))
			{
				offset = inputMap.getOffset(pos);
				sectorLength = inputMap.getLength(pos);
				//LOG.info("{},{}", offset, sectorLength);
			}
			else
			{

			// LOG.info(path.toString());
			
			int shortOffset = 4 * (pos.z + regionSize.z * (pos.y + regionSize.y * pos.x));
			
			input.seek(shortOffset);
			for (int i = 0; i < 3; i++) {
				offset *= 256;
				offset += input.read();
			}
			sectorLength = input.read();
			if (sectorLength == 0)
			{
				return null;
			}
			inputMap.putOffset(pos, offset);
			inputMap.putLength(pos, sectorLength);
			}
			input.seek(fullOffset + sectorSize * offset);

			// LOG.info("Read {} sectors", sectorLength);

			byte tempData[] = new byte[sectorSize * sectorLength];
			input.read(tempData);

			DataInputStream trueInput = new DataInputStream(
				new InflaterInputStream(new BufferedInputStream(new ByteArrayInputStream(tempData)))
			);
			DefaultChunkData chunk = ChunkIO.load(world, chunkPos, trueInput, IOContext.SAVE);
			readGenerationHint(chunk, trueInput, server);
			return chunk;
		}
		catch (EOFException e)
		{
			LOG.warn("Reached end of file, offset was {}, sectors was {}", offset, sectorLength);
			e.printStackTrace();
		}
		return null;
	}
	
	private DefaultChunkData loadRegionX(Path path, Vec3i chunkPos, DefaultWorldData world, Server server)
		throws IOException,
		DecodingException {
		int offset = 0;
		int sectorLength = 0;
		Vec3i pos;
		RandomFileMapped inputMap;
		int fullOffset = 4 * (chunksPerRegion);
		try
		{
			Vec3i streamCoords = getRegion(chunkPos);
			
			inputMap = inOutMap.get(new HashableVec3i(streamCoords));
			//LOG.info("streamCoords {},{},{}", streamCoords.x,streamCoords.y,streamCoords.z);
			if (inputMap == null)
			{
				//input = new RandomAccessFile(path.toFile(), "rw");
				//input = Files.newByteChannel(path);
				inputMap = makeNew(path, new HashableVec3i(streamCoords));
			}
			
			RandomAccessFile input = inputMap.file;
			
			
			pos = getRegionLoc(chunkPos);
			
			if (inputMap.hasOffset(pos))
			{
				offset = inputMap.getOffset(pos);
				sectorLength = inputMap.getLength(pos);
				//LOG.info("{},{}", offset, sectorLength);
			}
			else
			{

			// LOG.info(path.toString());
			
			int shortOffset = 4 * (pos.z + regionSize.z * (pos.y + regionSize.y * pos.x));
			
			input.seek(shortOffset);
			for (int i = 0; i < 3; i++) {
				offset *= 256;
				offset += input.read();
			}
			sectorLength = input.read();
			if (sectorLength == 0)
			{
				return null;
			}
			inputMap.putOffset(pos, offset);
			inputMap.putLength(pos, sectorLength);
			}
			input.seek(fullOffset + sectorSize * offset);
			
			int xByte = input.read();

			// LOG.info("Read {} sectors", sectorLength);

			byte tempData[] = new byte[sectorSize * sectorLength];
			input.read(tempData);

			DataInputStream trueInput = new DataInputStream(
				new InflaterInputStream(new BufferedInputStream(new ByteArrayInputStream(tempData)))
			);
			DefaultChunkData chunk = ChunkIO.load(world, chunkPos, trueInput, IOContext.SAVE);
			readGenerationHint(chunk, trueInput, server);
			
			chunk.isOpaque = (xByte & 2)==2;
			chunk.isEmpty = (xByte & 1)==1;
			
			return chunk;
		}
		catch (EOFException e)
		{
			LOG.warn("Reached end of file, offset was {}, sectors was {}", offset, sectorLength);
			e.printStackTrace();
		}
		return null;
	}

	private static void readGenerationHint(DefaultChunkData chunk, DataInputStream input, Server server)
		throws IOException,
		DecodingException {
		chunk.setGenerationHint(server.getWorld().getGenerator().readGenerationHint(input));
	}

	@Override
	public Path getPath() {
		return SAVE_DIR;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
