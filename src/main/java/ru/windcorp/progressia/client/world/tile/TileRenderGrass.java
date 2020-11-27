package ru.windcorp.progressia.client.world.tile;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerCube.OpaqueTile;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TileRenderGrass extends TileRender implements OpaqueTile {
	
	private final Texture topTexture;
	private final Texture sideTexture;
	
	public TileRenderGrass(
			String id,
			Texture top, Texture side
	) {
		super(id);
		this.topTexture = top;
		this.sideTexture = side;
	}

	@Override
	public Texture getTexture(BlockFace face) {
		return (face == BlockFace.TOP) ? topTexture : sideTexture;
	}
	
	@Override
	public boolean isOpaque(BlockFace face) {
		return face == BlockFace.TOP;
	}
	
	@Override
	public Renderable createRenderable(BlockFace face) {
		ShapeRenderProgram program = WorldRenderProgram.getDefault();
		
		return new Shape(
				Usage.STATIC, WorldRenderProgram.getDefault(),
				Faces.createBlockFace(
						program, getTexture(face), new Vec3(1, 1, 1),
						new Vec3(0, 0, 0), face, false
				)
		);
	}
	
	@Override
	public boolean needsOwnRenderable() {
		return false;
	}

}
