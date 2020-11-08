package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.collision.CompoundCollisionModel;

public class AABBRenderer {
	
	private static final Shape CUBE = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null).setColorMultiplier(1.0f, 0.7f, 0.2f).create();
	
	public static void renderAABB(AABB aabb, ShapeRenderHelper helper) {
		helper.pushTransform().translate(aabb.getOrigin()).scale(aabb.getSize());
		CUBE.render(helper);
		helper.popTransform();
	}
	
	public static void renderAABBsInCompound(
			CompoundCollisionModel model,
			ShapeRenderHelper helper
	) {
		for (CollisionModel part : model.getModels()) {
			if (part instanceof CompoundCollisionModel) {
				renderAABBsInCompound((CompoundCollisionModel) part, helper);
			} else if (part instanceof AABB) {
				renderAABB((AABB) part, helper);
			}
		}
	}

}
