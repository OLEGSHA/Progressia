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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import ru.windcorp.jputil.functions.ThrowingConsumer;
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.io.ChunkCodec;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;

public class TestChunkCodec extends ChunkCodec {

	private static class Palette<E> {
		private final List<E> nidToElement = new ArrayList<>();
		private final TObjectIntMap<E> elementToNid = new TObjectIntHashMap<>();

		public void add(E element) {
			if (elementToNid.containsKey(element))
				return;

			nidToElement.add(element);
			elementToNid.put(element, elementToNid.size());
		}

		public E getByNid(int nid) {
			return nidToElement.get(nid);
		}

		public int getNid(E element) {
			return elementToNid.get(element);
		}

		public int size() {
			return nidToElement.size();
		}
	}

	public TestChunkCodec() {
		super("Test:TestCodec", 0x00);
	}

	@Override
	public boolean shouldEncode(ChunkData chunk, IOContext context) {
		return true;
	}

	/*
	 * Decoding
	 */

	@Override
	public ChunkData decode(WorldData world, Vec3i position, DataInputStream input, IOContext context)
			throws DecodingException, IOException {
		BlockData[] blockPalette = readBlockPalette(input);
		TileData[] tilePalette = readTilePalette(input);

		ChunkData chunk = new ChunkData(position, world);
		readBlocks(input, blockPalette, chunk);
		readTiles(input, tilePalette, chunk);

		return chunk;
	}

	private BlockData[] readBlockPalette(DataInput input) throws IOException {
		BlockData[] palette = new BlockData[input.readInt()];

		for (int nid = 0; nid < palette.length; ++nid) {
			String id = input.readUTF();
			palette[nid] = BlockDataRegistry.getInstance().get(id);
		}

		return palette;
	}

	private TileData[] readTilePalette(DataInput input) throws IOException {
		TileData[] palette = new TileData[input.readInt()];

		for (int nid = 0; nid < palette.length; ++nid) {
			String id = input.readUTF();
			palette[nid] = TileDataRegistry.getInstance().get(id);
		}

		return palette;
	}

	private void readBlocks(DataInput input, BlockData[] blockPalette, ChunkData chunk) throws IOException {
		try {
			chunk.forEachBiC(guard(v -> {
				chunk.setBlock(v, blockPalette[input.readInt()], false);
			}));
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private void readTiles(DataInput input, TileData[] tilePalette, ChunkData chunk) throws IOException {
		Vec3i bic = new Vec3i();

		while (true) {
			int xOrEndMarker = input.readByte() & 0xFF;
			if (xOrEndMarker == 0xFF)
				break;

			bic.set(xOrEndMarker, input.readByte() & 0xFF, input.readByte() & 0xFF);
			BlockFace face = BlockFace.getFaces().get(input.readByte() & 0xFF);

			int tiles = input.readByte() & 0xFF;

			for (int i = 0; i < tiles; ++i) {
				TileData tile = tilePalette[input.readInt()];
				int tag = input.readInt();
				chunk.getTiles(bic, face).load(tile, tag);
			}
		}
	}

	/*
	 * Encoding
	 */

	@Override
	public void encode(ChunkData chunk, DataOutputStream output, IOContext context) throws IOException {
		Palette<BlockData> blockPalette = createBlockPalette(chunk);
		Palette<TileData> tilePalette = createTilePalette(chunk);

		writeBlockPalette(blockPalette, output);
		writeTilePalette(tilePalette, output);

		writeBlocks(chunk, blockPalette, output);
		writeTiles(chunk, tilePalette, output);
	}

	private Palette<BlockData> createBlockPalette(ChunkData chunk) {
		Palette<BlockData> blockPalette = new Palette<>();
		chunk.forEachBiC(v -> blockPalette.add(chunk.getBlock(v)));
		return blockPalette;
	}

	private Palette<TileData> createTilePalette(ChunkData chunk) {
		Palette<TileData> tilePalette = new Palette<>();
		chunk.forEachTile((ts, t) -> tilePalette.add(t));
		return tilePalette;
	}

	private void writeBlockPalette(Palette<BlockData> blockPalette, DataOutput output) throws IOException {
		output.writeInt(blockPalette.size());
		for (int nid = 0; nid < blockPalette.size(); ++nid) {
			BlockData block = blockPalette.getByNid(nid);
			output.writeUTF(block.getId());
		}
	}

	private void writeTilePalette(Palette<TileData> tilePalette, DataOutput output) throws IOException {
		output.writeInt(tilePalette.size());
		for (int nid = 0; nid < tilePalette.size(); ++nid) {
			TileData tile = tilePalette.getByNid(nid);
			output.writeUTF(tile.getId());
		}
	}

	private void writeBlocks(ChunkData chunk, Palette<BlockData> blockPalette, DataOutput output) throws IOException {
		try {
			chunk.forEachBiC(guard(v -> {
				output.writeInt(blockPalette.getNid(chunk.getBlock(v)));
			}));
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private void writeTiles(ChunkData chunk, Palette<TileData> tilePalette, DataOutput output) throws IOException {
		Vec3i bic = new Vec3i();

		try {
			chunk.forEachTileStack(guard(ts -> {
				if (ts.isEmpty())
					return;

				ts.getBlockInChunk(bic);
				output.writeByte(bic.x);
				output.writeByte(bic.y);
				output.writeByte(bic.z);

				output.writeByte(ts.getFace().getId());
				output.writeByte(ts.size());

				for (int index = 0; index < ts.size(); ++index) {
					output.writeInt(tilePalette.getNid(ts.get(index)));
					output.writeInt(ts.getTagByIndex(index));
				}
			}));
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}

		output.writeByte(0xFF);
	}

	private static <V> Consumer<V> guard(ThrowingConsumer<? super V, IOException> action) {
		return v -> {
			try {
				action.accept(v);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

}
