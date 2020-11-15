package ru.windcorp.progressia.common.collision;

import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;

import static java.lang.Math.*;

public class CollisionPathComputer {
	
	public static void forEveryBlockInCollisionPath(
			Collideable coll,
			float maxTime,
			Consumer<Vec3i> action
	) {
		Vec3 displacement = Vectors.grab3();
		coll.getCollideableVelocity(displacement);
		displacement.mul(maxTime);
		
		handleModel(coll.getCollisionModel(), displacement, action);
		
		Vectors.release(displacement);
	}

	private static void handleModel(
			CollisionModel model,
			Vec3 displacement,
			Consumer<Vec3i> action
	) {
		if (model instanceof CompoundCollisionModel) {
			for (CollisionModel subModel : ((CompoundCollisionModel) model).getModels()) {
				handleModel(subModel, displacement, action);
			}
		} else if (model instanceof AABBoid) {
			handleAABBoid((AABBoid) model, displacement, action);
		} else {
			throw new RuntimeException("not supported");
		}
	}

	private static void handleAABBoid(AABBoid model, Vec3 displacement, Consumer<Vec3i> action) {
		Vec3 size = Vectors.grab3();
		Vec3 origin = Vectors.grab3();
		
		model.getOrigin(origin);
		model.getSize(size);
		
		origin.mul(2).sub(size).div(2); // Subtract 0.5*size
		
		Vec3i pos = Vectors.grab3i();
		
		for (
				pos.x =  (int) floor(origin.x + min(0, size.x) + min(0, displacement.x));
				pos.x <= (int)  ceil(origin.x + max(0, size.x) + max(0, displacement.x));
				pos.x += 1
		) {
			for (
					pos.y =  (int) floor(origin.y + min(0, size.y) + min(0, displacement.y));
					pos.y <= (int)  ceil(origin.y + max(0, size.y) + max(0, displacement.y));
					pos.y += 1
			) {
				for (
						pos.z =  (int) floor(origin.z + min(0, size.z) + min(0, displacement.z));
						pos.z <= (int)  ceil(origin.z + max(0, size.z) + max(0, displacement.z));
						pos.z += 1
				) {
					action.accept(pos);
				}
			}
		}
		
		Vectors.release(origin);
		Vectors.release(size);
		Vectors.release(pos);
	}

}
