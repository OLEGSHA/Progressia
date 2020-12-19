package ru.windcorp.progressia.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public abstract class ChunkCodec extends Namespaced {
	
	private final byte signature;
	
	public ChunkCodec(String id, byte signature) {
		super(id);
		this.signature = signature;
	}
	
	public ChunkCodec(String id, int signature) {
		this(id, (byte) signature);
	}
	
	public byte getSignature() {
		return signature;
	}

	public abstract ChunkData decode(WorldData world, Vec3i position, InputStream data) throws DecodingException, IOException;
	
	public abstract boolean shouldEncode(ChunkData chunk);
	
	public abstract void encode(ChunkData chunk, OutputStream output) throws IOException;

}
