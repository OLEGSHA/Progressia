package ru.windcorp.progressia.common.world;

import java.util.AbstractList;
import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

class ChunkDataReportingList extends AbstractList<TileData> {
	
	private final List<TileData> parent;
	private final ChunkData reportTo;
	private final Vec3i blockInChunk;
	private final BlockFace face;

	public ChunkDataReportingList(List<TileData> parent, ChunkData reportTo, Vec3i blockInChunk, BlockFace face) {
		super();
		this.parent = parent;
		this.reportTo = reportTo;
		this.blockInChunk = new Vec3i(blockInChunk);
		this.face = face;
	}

	@Override
	public TileData get(int index) {
		return parent.get(index);
	}

	@Override
	public int size() {
		return parent.size();
	}
	
	@Override
	public TileData set(int index, TileData element) {
		TileData previous = parent.set(index, element);
		report(previous, element);
		return previous;
	}
	
	@Override
	public void add(int index, TileData element) {
		parent.add(index, element);
		report(null, element);
	}
	
	@Override
	public TileData remove(int index) {
		TileData previous = parent.remove(index);
		report(previous, null);
		return previous;
	}
	
	private void report(TileData previous, TileData current) {
		reportTo.getListeners().forEach(l -> {
			if (previous != null) {
				l.onChunkTilesChanged(reportTo, blockInChunk, face, previous, false);
			}
			
			if (current != null) {
				l.onChunkTilesChanged(reportTo, blockInChunk, face, current, true);
			}
			
			l.onChunkChanged(reportTo);
		});
	}

}
