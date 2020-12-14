package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.common.world.tile.TileReference;

public interface TileTickContext extends TSTickContext {
	
	/*
	 * Specifications
	 */
	
	/**
	 * Returns the current layer.
	 * @return the layer that the tile being ticked occupies in the tile stack
	 */
	int getLayer();
	
	/*
	 * Getters
	 */
	
	default TileLogic getTile() {
		TileLogicStack stack = getTLSOrNull();
		if (stack == null) return null;
		return stack.get(getLayer());
	}
	
	default TileData getTileData() {
		TileDataStack stack = getTDSOrNull();
		if (stack == null) return null;
		return stack.get(getLayer());
	}
	
	default TileReference getReference() {
		return getTDS().getReference(getLayer());
	}
	
	default int getTag() {
		return getTDS().getTagByIndex(getLayer());
	}
	
	/*
	 * Contexts
	 */
	
	/*
	 * Convenience methods - changes
	 */
	
	default void removeThisTile() {
		getAccessor().removeTile(getBlockInWorld(), getFace(), getTag());
	}

}
