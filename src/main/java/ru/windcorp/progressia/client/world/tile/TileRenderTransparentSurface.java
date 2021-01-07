package ru.windcorp.progressia.client.world.tile;

import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TileRenderTransparentSurface extends TileRenderSurface {

	public TileRenderTransparentSurface(String id, Texture texture) {
		super(id, texture);
	}

	@Override
	public boolean isOpaque(BlockFace face) {
		return false;
	}

}
