package ru.windcorp.progressia.server.world.generation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;

public abstract class AbstractWorldGenerator<H> extends WorldGenerator {
	
	private final Class<H> hintClass;

	public AbstractWorldGenerator(String id, Class<H> hintClass) {
		super(id);
		this.hintClass = Objects.requireNonNull(hintClass, "hintClass");
	}
	
	@Override
	public final Object readGenerationHint(DataInputStream input) throws IOException, DecodingException {
		return doReadGenerationHint(input);
	}
	
	@Override
	public final void writeGenerationHint(DataOutputStream output, Object hint) throws IOException {
		doWriteGenerationHint(output, hintClass.cast(hint));
	}
	
	protected abstract H doReadGenerationHint(DataInputStream input) throws IOException, DecodingException;
	protected abstract void doWriteGenerationHint(DataOutputStream output, H hint) throws IOException;
	
	@Override
	public final boolean isChunkReady(Object hint) {
		return checkIsChunkReady(hintClass.cast(hint));
	}
	
	protected abstract boolean checkIsChunkReady(H hint);
	
	protected H getHint(ChunkData chunk) {
		return hintClass.cast(chunk.getGenerationHint());
	}
	
	protected void setHint(ChunkData chunk, H hint) {
		chunk.setGenerationHint(hint);
	}

}
