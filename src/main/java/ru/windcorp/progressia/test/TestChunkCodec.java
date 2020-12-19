package ru.windcorp.progressia.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.io.ChunkCodec;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class TestChunkCodec extends ChunkCodec {
	
	public TestChunkCodec() {
		super("Test:TestCodec", 0x00);
	}

	@Override
	public ChunkData decode(WorldData world, Vec3i position, InputStream data) throws DecodingException, IOException {
		ChunkData chunk = new ChunkData(position, world);
		TestContent.generateChunk(chunk);
		return chunk;
	}

	@Override
	public boolean shouldEncode(ChunkData chunk) {
		return true;
	}

	@Override
	public void encode(ChunkData chunk, OutputStream output) throws IOException {
		// Do nothing. Heh.
	}

}
