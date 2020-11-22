package ru.windcorp.progressia.client.world.tile;

import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizer;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TileRender extends Namespaced {

	public TileRender(String id) {
		super(id);
	}
	
	public void render(ShapeRenderHelper renderer, BlockFace face) {
		throw new UnsupportedOperationException(
				"TileRender.render() not implemented in " + this
		);
	}
	
	public Renderable createRenderable(BlockFace face) {
		return null;
	}
	
	public boolean canBeOptimized(ChunkRenderOptimizer optimizer) {
		return true;
	}
	
	public boolean needsOwnRenderable() {
		return true;
	}

}
