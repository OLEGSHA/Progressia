package ru.windcorp.progressia.client.graphics.world;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.world.Camera.Anchor;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class EntityAnchor implements Anchor {
	
	private final EntityData entity;
	private final EntityRenderable model;
	
	private final Collection<Mode> modes;

	public EntityAnchor(EntityRenderable model) {
		this.entity = model.getData();
		this.model = model;
		
		this.modes = ImmutableList.of(
				// From viewpoint / first person
				Mode.of(v -> v.set(0), m -> {}),
				
				// Third person, looking forward
				Mode.of(
						v -> v.set(-3.5f, +0.5f, 0),
						m -> {}
				),
				
				// Third person, looking back
				Mode.of(
						v -> v.set(-3.5f, 0, 0),
						m -> m.rotateZ((float) Math.PI)
				)
		);
	}

	@Override
	public void getCameraPosition(Vec3 output) {
		model.getViewPoint(output);
		output.add(entity.getPosition());
	}

	@Override
	public void getCameraVelocity(Vec3 output) {
		output.set(entity.getVelocity());
	}

	@Override
	public float getCameraYaw() {
		return entity.getYaw();
	}

	@Override
	public float getCameraPitch() {
		return entity.getPitch();
	}

	@Override
	public Collection<Mode> getCameraModes() {
		return modes;
	}

}
