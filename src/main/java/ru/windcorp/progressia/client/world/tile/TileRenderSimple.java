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

public class TileRenderSimple extends TileRender implements OpaqueTile {
	
	private final Texture texture;
	private final boolean opaque;
	
	public TileRenderSimple(String id, Texture texture, boolean opaque) {
		super(id);
		this.texture = texture;
		this.opaque = opaque;
	}
	
	public TileRenderSimple(String id, Texture texture) {
		this(id, texture, false);
	}

	@Override
	public Texture getTexture(BlockFace face) {
		return texture;
	}
	
	@Override
	public boolean isOpaque(BlockFace face) {
		return opaque;
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
