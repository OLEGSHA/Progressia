package ru.windcorp.progressia.common.world.tile;

import java.util.AbstractList;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;

public abstract class GenericTileStack<T extends Namespaced, C>
extends AbstractList<T>
implements RandomAccess {
	
	public static interface TSConsumer<T> {
		void accept(int layer, T tile);
	}

	public static final int TILES_PER_FACE = 8;
	
	public abstract Vec3i getBlockInChunk(Vec3i output);
	protected abstract Vec3i getChunkPos();
	public abstract C getChunk();
	public abstract BlockFace getFace();
	
	public Vec3i getBlockInWorld(Vec3i output) {
		// This is safe
		return Coordinates.getInWorld(getChunkPos(), getBlockInChunk(output), output);
	}
	
	public boolean isFull() {
		return size() >= TILES_PER_FACE;
	}
	
	public T getClosest() {
		return get(0);
	}
	
	public T getFarthest() {
		return get(size() - 1);
	}

	public void forEach(TSConsumer<T> action) {
		Objects.requireNonNull(action, "action");
		for (int i = 0; i < size(); ++i) {
			action.accept(i, get(i));
		}
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		Objects.requireNonNull(action, "action");
		for (int i = 0; i < size(); ++i) {
			action.accept(get(i));
		}
	}
	
	public T findClosest(String id) {
		Objects.requireNonNull(id, "id");
		
		for (int i = 0; i < size(); ++i) {
			T tile = get(i);
			if (tile.getId().equals(id)) {
				return tile;
			}
		}
		
		return null;
	}
	
	public T findFarthest(String id) {
		Objects.requireNonNull(id, "id");
		
		for (int i = 0; i < size(); ++i) {
			T tile = get(i);
			if (tile.getId().equals(id)) {
				return tile;
			}
		}
		
		return null;
	}
	
	public boolean contains(String id) {
		return findClosest(id) != null;
	}

}
