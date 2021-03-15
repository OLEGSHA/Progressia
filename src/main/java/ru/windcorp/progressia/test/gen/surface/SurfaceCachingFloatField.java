/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.windcorp.progressia.test.gen.surface;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.rels.AbsFace;

/**
 * A scalar field defined on a plane. For each pair of {@code float}s (north; west) a single {@code float} is defined by this object. 
 */
public abstract class SurfaceCachingFloatField extends Namespaced implements SurfaceFloatField {
	
	private final int levels;
	
	private SurfaceFieldRegistry registry = null;
	private int index;
	
	public SurfaceCachingFloatField(String id, int levels) {
		super(id);
		this.levels = levels;
	}
	
	int getIndex() {
		if (getRegistry() == null) {
			throw new IllegalStateException("No registry assigned to field " + this);
		}
		return index;
	}
	
	void setIndex(int index) {
		if (getRegistry() == null) {
			throw new IllegalStateException("No registry assigned to field " + this);
		}
		this.index = index;
	}
	
	SurfaceFieldRegistry getRegistry() {
		return registry;
	}
	
	void setRegistry(SurfaceFieldRegistry registry) {
		this.registry = registry;
	}
	
	public int getLevels() {
		return levels;
	}
	
	protected abstract float computeDetailAt(AbsFace surface, int level, float north, float west);
	
	@Override
	public float get(AbsFace surface, float north, float west) {
		float result = 0;
		
		for (int level = 0; level < getLevels(); ++level) {
			result += computeDetailAt(surface, level, north, west);
		}
		
		return result;
	}

}
