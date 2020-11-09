package ru.windcorp.progressia.common.collision;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import glm.vec._3.Vec3;

public class CompoundCollisionModel implements CollisionModel {
	
	private final Collection<CollisionModel> models;

	public CompoundCollisionModel(Collection<CollisionModel> models) {
		this.models = models;
	}
	
	public CompoundCollisionModel(CollisionModel... models) {
		this(ImmutableList.copyOf(models));
	}
	
	public Collection<CollisionModel> getModels() {
		return models;
	}

	@Override
	public void setOrigin(Vec3 origin) {
		for (CollisionModel model : getModels()) {
			model.setOrigin(origin);
		}
	}

	@Override
	public void moveOrigin(Vec3 displacement) {
		for (CollisionModel model : getModels()) {
			model.moveOrigin(displacement);
		}
	}

}
