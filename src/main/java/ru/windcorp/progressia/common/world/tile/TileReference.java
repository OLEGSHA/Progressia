package ru.windcorp.progressia.common.world.tile;

public interface TileReference {
	
	TileData get();
	int getIndex();
	TileDataStack getStack();
	
	default boolean isValid() {
		return get() != null;
	}

}
