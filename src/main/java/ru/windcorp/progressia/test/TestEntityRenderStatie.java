package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.entity.EntityRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class TestEntityRenderStatie extends EntityRender {
	
	private final Renderable cube =
			new Shapes.PppBuilder(
					WorldRenderProgram.getDefault(),
					(Texture) null
			)
			.setColorMultiplier(1, 1, 0)
			.create();

	public TestEntityRenderStatie(String id) {
		super(id);
	}

	@Override
	public EntityRenderable createRenderable(EntityData entity) {
		return new EntityRenderable(entity) {
			@Override
			public void render(ShapeRenderHelper renderer) {
				renderer.pushTransform().scale(
						((TestEntityDataStatie) entity).getSize() / 24.0f
				);
				
				cube.render(renderer);
				
				renderer.popTransform();
			}
		};
	}

}
