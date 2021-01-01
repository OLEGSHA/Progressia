package ru.windcorp.progressia.common.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import glm.vec._3.i.Vec3i;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class ChunkIO {
	
	private static final TByteObjectMap<ChunkCodec> CODECS_BY_ID = new TByteObjectHashMap<>();
	private static final List<ChunkCodec> CODECS_BY_PRIORITY = new ArrayList<>();
	
	public static ChunkData load(WorldData world, Vec3i position, InputStream data)
			throws DecodingException, IOException
	{
		if (CODECS_BY_ID.isEmpty()) throw new IllegalStateException("No codecs registered");
		
		int signature = data.read();
		if (signature < 0) throw new EOFException("Expected codec signature, got EOF");
		
		ChunkCodec codec = getCodec((byte) signature);
		if (codec == null) {
			throw new DecodingException("Unknown codec signature " + Integer.toHexString(signature) + "; is it from the future?");
		}
		
		try {
			return codec.decode(world, position, data);
		} catch (IOException | DecodingException e) {
			throw e;
		} catch (Throwable t) {
			throw CrashReports.report(
					t, "Codec %s has failed to decode chunk (%d; %d; %d)",
					codec.getId(), position.x, position.y, position.z
			);
		}
	}
	
	public static void save(ChunkData chunk, OutputStream output)
			throws IOException
	{
		ChunkCodec codec = getCodec(chunk);
		
		try {
			output.write(codec.getSignature());
			codec.encode(chunk, output);
		} catch (IOException e) {
			throw e;
		} catch (Throwable t) {
			throw CrashReports.report(
					t, "Codec %s has failed to encode chunk (%d; %d; %d)",
					codec.getId(), chunk.getPosition().x, chunk.getPosition().y, chunk.getPosition().z
			);
		}
	}
	
	public static ChunkCodec getCodec(byte signature) {
		if (CODECS_BY_ID.isEmpty()) throw new IllegalStateException("No codecs registered");
		return CODECS_BY_ID.get(signature);
	}
	
	public static ChunkCodec getCodec(ChunkData chunk) {
		for (ChunkCodec codec : CODECS_BY_PRIORITY) {
			if (codec.shouldEncode(chunk)) {
				return codec;
			}
		}

		if (CODECS_BY_ID.isEmpty()) throw new IllegalStateException("No codecs registered");
		return CODECS_BY_PRIORITY.get(0);
	}

	/**
	 * Sorted is order of decreasing priority 
	 * @return
	 */
	public static List<ChunkCodec> getCodecs() {
		return Collections.unmodifiableList(CODECS_BY_PRIORITY);
	}
	
	public static void registerCodec(ChunkCodec codec) {
		CODECS_BY_PRIORITY.add(0, codec); // Add to the front
		CODECS_BY_ID.put(codec.getSignature(), codec);
	}
	
	private ChunkIO() {}

}
