package ru.windcorp.progressia.test;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.common.collision.AABBoid;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.collision.CompoundCollisionModel;
import ru.windcorp.progressia.common.util.Vectors;

public class CollisionModelRenderer {
	
	private static final Shape CUBE = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null).setColorMultiplier(1.0f, 0.7f, 0.2f).create();
	private static final Shape CUBE_GRAY = new Shapes.PppBuilder(WorldRenderProgram.getDefault(), (Texture) null).setColorMultiplier(0.5f, 0.5f, 0.5f).create();
	
	public static void renderCollisionModel(CollisionModel model, ShapeRenderHelper helper) {
		if (model instanceof AABBoid) {
			renderAABBoid((AABBoid) model, helper);
		} else if (model instanceof CompoundCollisionModel) {
			renderCompound((CompoundCollisionModel) model, helper);
		} else {
			// Ignore silently
		}
	}
	
	private static void renderAABBoid(AABBoid aabb, ShapeRenderHelper helper) {
		Mat4 mat = helper.pushTransform();
		Vec3 tmp = Vectors.grab3();
		
		aabb.getOrigin(tmp);
		mat.translate(tmp);
		aabb.getSize(tmp);
		mat.scale(tmp);
		
		Vectors.release(tmp);
		
		CUBE.render(helper);
		helper.popTransform();
	}
	
	private static void renderCompound(
			CompoundCollisionModel model,
			ShapeRenderHelper helper
	) {
		for (CollisionModel part : model.getModels()) {
			renderCollisionModel(part, helper);
		}
	}
	
	public static void renderBlock(Vec3i coords, ShapeRenderHelper helper) {
		helper.pushTransform().translate(coords.x, coords.y, coords.z);
		CUBE_GRAY.render(helper);
		helper.popTransform();
	}

}
