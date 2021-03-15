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
package ru.windcorp.progressia.test.gen.planet;

import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.test.gen.surface.SurfaceFloatField;

public class TestHeightMap implements SurfaceFloatField {
	
	private final float cutoffPoint;
	private final float cutoffDistance;
	private final float amplitude;
	private final float characteristicSize;

	public TestHeightMap(
		float cutoffPoint,
		float cutoffDistance,
		float amplitude,
		float characteristicSize
	) {
		this.cutoffPoint = cutoffPoint;
		this.cutoffDistance = cutoffDistance;
		this.amplitude = amplitude;
		this.characteristicSize = characteristicSize;
	}

	@Override
	public float get(AbsFace face, float north, float west) {
		double cutoffCoefficient = 1;
		cutoffCoefficient *= cutoffFunction(cutoffPoint - north);
		cutoffCoefficient *= cutoffFunction(cutoffPoint + north);
		cutoffCoefficient *= cutoffFunction(cutoffPoint - west);
		cutoffCoefficient *= cutoffFunction(cutoffPoint + west);
		
		if (cutoffCoefficient == 0) {
			return 0;
		}
		
		double base = Math.sin(north / characteristicSize) * Math.sin(west / characteristicSize);
		base *= amplitude;
		
		return (float) (base * cutoffCoefficient);
	}
	
	private double cutoffFunction(float distanceToCutoffPoint) {
		if (distanceToCutoffPoint < 0) {
			return 0;
		} else if (distanceToCutoffPoint < cutoffDistance) {
			return (1 - Math.cos(Math.PI * distanceToCutoffPoint / cutoffDistance)) / 2;
		} else {
			return 1;
		}
	}

}
