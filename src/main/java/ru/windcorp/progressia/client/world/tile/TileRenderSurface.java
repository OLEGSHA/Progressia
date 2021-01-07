package ru.windcorp.progressia.client.world.tile;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerCube.OpaqueSurface;
import ru.windcorp.progressia.common.world.block.BlockFace;

public abstract class TileRenderSurface extends TileRender implements OpaqueSurface {
	
	private final Texture texture;
	
	public TileRenderSurface(String id, Texture texture) {
		super(id);
		this.texture = texture;
	}

	@Override
	public Texture getTexture(BlockFace face) {
		return texture;
	}
	
	@Override
	public Renderable createRenderable(BlockFace face) {
		ShapeRenderProgram program = WorldRenderProgram.getDefault();
		
		return new Shape(
				Usage.STATIC, WorldRenderProgram.getDefault(),
				Faces.createBlockFace(
						program, getTexture(face), Colors.WHITE,
						new Vec3(0, 0, 0), face, false
				)
		);
	}
	
	@Override
	public boolean needsOwnRenderable() {
		return false;
	}

}
