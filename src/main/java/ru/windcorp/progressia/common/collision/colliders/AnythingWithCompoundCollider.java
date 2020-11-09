package ru.windcorp.progressia.common.collision.colliders;

import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.collision.CompoundCollisionModel;
import ru.windcorp.progressia.common.collision.colliders.Collider.ColliderWorkspace;
import ru.windcorp.progressia.common.collision.colliders.Collider.Collision;

class AnythingWithCompoundCollider {
	
	static Collider.Collision computeModelCollision(
			Collideable aBody, Collideable bBody,
			CompoundCollisionModel aModel, CollisionModel bModel,
			float tickLength,
			ColliderWorkspace workspace
	) {
		Collision result = null;
		
		for (CollisionModel aModelPart : aModel.getModels()) {
				
			Collision collision = Collider.getCollision(
					aBody, bBody,
					aModelPart, bModel,
					tickLength, workspace
			);
			
			// Update result
			if (collision != null) {
				Collision second;
				
				if (result == null || collision.time < result.time) {
					second = result;
					result = collision;
				} else {
					second = collision;
				}
				
				// Release Collision that is no longer used
				if (second != null) workspace.release(second);
			}
				
		}
		
		return result;
	}

}
