package ru.windcorp.progressia.client.world.renders;

import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.client.world.renders.cro.ChunkRenderOptimizer;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.util.Namespaced;

public class TileRender extends Namespaced {

	public TileRender(String namespace, String name) {
		super(namespace, name);
	}
	
	public void render(ShapeRenderHelper renderer, BlockFace face) {
		throw new UnsupportedOperationException(
				"TileRender.render() not implemented in " + this
		);
	}
	
	public WorldRenderable createRenderable(BlockFace face) {
		return null;
	}
	
	public boolean canBeOptimized(ChunkRenderOptimizer optimizer) {
		return true;
	}
	
	public boolean needsOwnRenderable() {
		return true;
	}

}
