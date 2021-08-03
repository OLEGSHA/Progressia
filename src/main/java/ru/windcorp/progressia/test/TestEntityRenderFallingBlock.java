package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.block.BlockRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

/**
 * Renderer for Test:FallingBlock
 * 
 * @author opfromthestart
 *
 */
public class TestEntityRenderFallingBlock extends EntityRender {
	private Renderable cube;

	public TestEntityRenderFallingBlock(String id) {
		super(id);
		cube = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), BlockRenderRegistry.getBlockTexture(id) )
						.create();
	}

	public void setTexture(Texture texture) { // There has to be a better way.
		cube = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), texture).create();
	}

	@Override
	public EntityRenderable createRenderable(EntityData entity) {
		return new EntityRenderable(entity) {
			@Override
			public void render(ShapeRenderHelper renderer) {
				// LogManager.getLogger().info("Rendering FallingBlock");
				if (((TestEntityDataFallingBlock) entity).isDone()) {
					return;
					// setTexture(new
					// SimpleTexture(Atlases.getSprite(ResourceManager.getTextureResource("blocks/LogSide"),
					// new AtlasGroup("Blocks", 1 << 12))));
				}
				// setTexture(new
				// SimpleTexture(Atlases.getSprite(ResourceManager.getTextureResource("blocks/Sand"),
				// new AtlasGroup("Blocks", 1 << 12))));
				cube.render(renderer);
			}
		};
	}

}
