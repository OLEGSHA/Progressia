package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.state.IntStateField;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class TestEntityDataStatie extends EntityData {
	
	private final IntStateField size =
			field("Test", "Size").setShared().ofInt().build();

	public TestEntityDataStatie() {
		super("Test", "Statie");
		setCollisionModel(new AABB(0, 0, 0, 16f/24, 16f/24, 16f/24));
		setSizeNow(16);
	}
	
	public int getSize() {
		return size.get(this);
	}
	
	public void setSizeNow(int size) {
		this.size.setNow(this, size);
	}
	
	@Override
	public float getCollisionMass() {
		return 50f;
	}

}
