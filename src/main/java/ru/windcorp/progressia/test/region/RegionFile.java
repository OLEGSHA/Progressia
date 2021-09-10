package ru.windcorp.progressia.test.region;

import static ru.windcorp.progressia.test.region.TestWorldDiskIO.REGION_DIAMETER;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.generic.ChunkMap;

/**Backend for the .progressia_region file.
 * Use similarly to a file object
 * 
 */
public class RegionFile {
	// 4 MiB
	private static final int MAX_CHUNK_SIZE = 4 * 1024 * 1024;
	private static final int SECTORS_BYTES = Short.BYTES;
	private static final int SECTOR_SIZE = MAX_CHUNK_SIZE >> (SECTORS_BYTES*8);
	private static final int SECTOR_HEADER_SIZE = 1;
	
	final byte endBytes[] = new byte[SECTOR_SIZE];
	
	public static enum SectorType
	{
		Ending (0), // Just an empty block
		Data (1), // has a byte counting up in position 1, and then
		PartitionLink (2),
		BulkSaved (3); // TODO implement this
		
		private final byte data;
		
		SectorType(int i)
		{
			this.data = (byte) i;
		}
		
	}
	
	private static final int DEFINITION_SIZE = Integer.BYTES;

	private static final int HEADER_SIZE = DEFINITION_SIZE * REGION_DIAMETER * REGION_DIAMETER * REGION_DIAMETER;
	
	private final RandomAccessFile file;
	
	public RegionFile(RandomAccessFile inFile)
	{
		file = inFile;
	}
	
	public void confirmHeaderHealth(ChunkMap<Integer> offsets) throws IOException {

		Set<Integer> used = new HashSet<Integer>();
		int maxUsed = 0;
		final int chunksPerRegion = REGION_DIAMETER * REGION_DIAMETER * REGION_DIAMETER;

		if (file.length() < HEADER_SIZE) {
			throw new IOException("File is too short to contain a header");
		}

		for (int i = 0; i < chunksPerRegion; i++) { 
			file.seek(i*DEFINITION_SIZE);
			int offset = file.readInt();

			if (offset == 0) {
				continue;
			}
			
			offset--;

			Vec3i pos = new Vec3i();
			pos.x = i / REGION_DIAMETER / REGION_DIAMETER;
			pos.y = (i / REGION_DIAMETER) % REGION_DIAMETER;
			pos.z = i % REGION_DIAMETER;

			offsets.put(pos, offset);
			
			boolean shouldEnd = false;
			byte counter = 0;
			while (!shouldEnd)
			{
				if (offset > maxUsed)
				{
					maxUsed = offset;
				}

				if (!used.add(offset)) {
					throw new IOException("A sector is used twice");
				}
				
				file.seek(HEADER_SIZE + SECTOR_SIZE*offset);
				byte type = file.readByte();
				
				if (type == SectorType.Data.data)
				{
					byte fileCounter = file.readByte();
					if (fileCounter != counter)
					{
						throw new IOException("An unexpected block was found");
					}
					counter++;
					offset++;
				}
				else if (type == SectorType.Ending.data) {
					shouldEnd = true;
				}
				else if (type == SectorType.PartitionLink.data)
				{
					offset = file.readInt();
				}
				
			}
		}
		LogManager.getLogger("Region").debug("Efficiency of {}", (double) used.size()/maxUsed);
	}
	
	public void makeHeader() throws IOException
	{
		file.seek(0);
		for (int i=0;i<HEADER_SIZE;i++)
		{
			file.write(0);
		}
	}
	
	public void writeBuffer(byte[] buffer, int dataOffset, Vec3i pos) throws IOException {
		file.seek(HEADER_SIZE + SECTOR_SIZE * dataOffset);
		int loc=0;
		byte tempBuffer[] = new byte[SECTOR_SIZE];
		byte counter = 0;
		boolean isDone = false;
		while (!isDone)
		{
			if (file.length() > HEADER_SIZE + SECTOR_SIZE * (dataOffset + 1)) {
				file.seek(HEADER_SIZE + SECTOR_SIZE * (dataOffset + 1));
				byte header = file.readByte();
				if (header == SectorType.Data.data) {
					byte fileCounter = file.readByte();
					if (fileCounter != counter+1) // This makes the actual
												// partition place
					{
						int newOffset = allocateEmptySector();
						file.seek(HEADER_SIZE + SECTOR_SIZE * dataOffset);
						file.write(2);
						file.writeInt(newOffset);
						dataOffset = newOffset;
					}
				}
			}
			tempBuffer[0] = 1;
			tempBuffer[1] = counter;
			counter++;
			for (int i=0;i<(SECTOR_SIZE-SECTOR_HEADER_SIZE-1);i++)
			{
				if (loc*(SECTOR_SIZE-SECTOR_HEADER_SIZE-1) + i<buffer.length)
				{
					tempBuffer[i+SECTOR_HEADER_SIZE+1] = buffer[loc*(SECTOR_SIZE-SECTOR_HEADER_SIZE-1) + i];
				}
				else
				{
					isDone = true;
					break;
				}
			}
			loc++;
			if (file.getFilePointer()<256)
				LogManager.getLogger("Region").debug("at {}, ({},{},{}), {}", file.getFilePointer(),pos.x,pos.y,pos.z, dataOffset);
			file.seek(HEADER_SIZE + SECTOR_SIZE * dataOffset);
			dataOffset++;
			file.write(tempBuffer);
		}
		
		file.write(endBytes);
	}
	
	public int allocateChunk( Vec3i pos) throws IOException {
		int definitionOffset = DEFINITION_SIZE * (pos.z + REGION_DIAMETER * (pos.y + REGION_DIAMETER * pos.x));
		
		int outputLen = (int) file.length();

		int dataOffset = (int) Math.ceil((double) (outputLen - HEADER_SIZE) / SECTOR_SIZE);

		file.seek(definitionOffset);
		file.writeInt(dataOffset+1);

		file.setLength(HEADER_SIZE + dataOffset * SECTOR_SIZE);
		return dataOffset;
	}
	
	private int allocateEmptySector() throws IOException
	{
		int outputLen = (int) file.length();

		int dataOffset = (int) Math.ceil((double) (outputLen - HEADER_SIZE) / SECTOR_SIZE);
		
		file.setLength(HEADER_SIZE + dataOffset * SECTOR_SIZE);
		
		return dataOffset;
	}

	public byte[] readBuffer(int dataOffset) throws IOException {
		file.seek(HEADER_SIZE + SECTOR_SIZE * dataOffset);

		int bufferPos = 0;
		byte buffer[] = new byte[SECTOR_SIZE*16];
		byte tempBuffer[] = new byte[SECTOR_SIZE];
		
		boolean reachedEnd = false;
		byte counter = 0;
		while (!reachedEnd)
		{
			int bytesRead = file.read(tempBuffer, 0, SECTOR_SIZE);
			if (bytesRead<0)
			{
				reachedEnd = true;
				continue;
			}
			if (tempBuffer[0] == SectorType.Data.data)
			{
				if (tempBuffer[1] != counter)
				{
					throw new IOException("Sectors were read out of order\nExpected chunk number "+Byte.toString(counter)+" but encountered number " + Byte.toString(tempBuffer[1]));
				}
				counter++;
				if (buffer.length - bufferPos < SECTOR_SIZE-SECTOR_HEADER_SIZE-1)
				{
					byte newBuffer[] = new byte[buffer.length + SECTOR_SIZE*16];
					for (int i=0;i<buffer.length;i++)     // TODO dedicated copy, java-y at least
					{
						newBuffer[i] = buffer[i];
					}
					buffer = newBuffer;
				}
				for (int i=0;i<SECTOR_SIZE-SECTOR_HEADER_SIZE-1;i++)
				{
					buffer[bufferPos+i] = tempBuffer[i+2];
				}
				bufferPos += SECTOR_SIZE-SECTOR_HEADER_SIZE-1;
			}
			else if (tempBuffer[0] == SectorType.Ending.data)
			{
				reachedEnd = true;
			}
			else if (tempBuffer[0] == SectorType.PartitionLink.data)
			{
				ByteBuffer intBuffer = ByteBuffer.wrap(tempBuffer);
				int newOffset = intBuffer.getInt(1);
				file.seek(HEADER_SIZE + SECTOR_SIZE * newOffset);
			}
			else
			{
				throw new IOException("Invalid sector ID.");
			}
		}
		return buffer;
	}

	public void close() throws IOException {
		file.close();
	}
	
}
