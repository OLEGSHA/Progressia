package ru.windcorp.progressia.client.world.renders;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.util.Vectors;

public class TileRenderSimple extends TileRender {
	
	private final Texture texture;
	
	public TileRenderSimple(String namespace, String name, Texture texture) {
		super(namespace, name);
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}
	
	@Override
	public WorldRenderable createRenderable(BlockFace face) {
		ShapeRenderProgram program = WorldRenderProgram.getDefault();
		
		Vec3 color = Vectors.grab3().set(1, 1, 1);
		Vec3 center = Vectors.grab3().set(0, 0, 0);
		
		try {
			return new Shape(
					Usage.STATIC, WorldRenderProgram.getDefault(),
					Faces.createBlockFace(
							program, texture, color, center, face, false
					)
			);
		} finally {
			Vectors.release(color);
			Vectors.release(center);
		}
	}

}
