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
package ru.windcorp.progressia.common.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import glm.vec._2.Vec2;

public class PiecewiseLinearFunction implements FloatFunction {
	
	public static class Builder {
		
		private final List<Vec2> points = new ArrayList<>();
		private float slopeAtNegInf = 0;
		private float slopeAtPosInf = 0;
		
		public Builder add(float x, float y) {
			points.add(new Vec2(x, y));
			return this;
		}
		
		public Builder setNegativeSlope(float slope) {
			slopeAtNegInf = slope;
			return this;
		}
		
		public Builder setPositiveSlope(float slope) {
			slopeAtPosInf = slope;
			return this;
		}
		
		public Builder setDefaultUndefined() {
			slopeAtPosInf = Float.NaN;
			slopeAtNegInf = Float.NaN;
			return this;
		}
		
		public PiecewiseLinearFunction build() {
			float[] pointXs = new float[points.size()];
			float[] pointYs = new float[points.size()];
			
			points.sort(Comparator.comparingDouble(v -> v.x));
			for (int i = 0; i < points.size(); ++i) {
				pointXs[i] = points.get(i).x;
				pointYs[i] = points.get(i).y;
			}
			
			return new PiecewiseLinearFunction(pointXs, pointYs, slopeAtNegInf, slopeAtPosInf);
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	/**
	 * The set of the X coordinates of all defining points, sorted in increasing order
	 */
	private final float[] pointXs;
	
	/**
	 * The set of the Y coordinates of all defining points, sorted to match the order of {@link #pointXs}
	 */
	private final float[] pointYs;
	
	/**
	 * Slope of the segment (-inf; x[0]), or NaN to exclude the segment from the function
	 */
	private final float slopeAtNegInf;
	
	/**
	 * Slope of the segment (x[x.length - 1]; +inf), or NaN to exclude the segment from the function
	 */
	private final float slopeAtPosInf;

	protected PiecewiseLinearFunction(float[] pointXs, float[] pointYs, float slopeAtNegInf, float slopeAtPosInf) {
		this.pointXs = pointXs;
		this.pointYs = pointYs;
		this.slopeAtNegInf = slopeAtNegInf;
		this.slopeAtPosInf = slopeAtPosInf;
	}

	@Override
	public float apply(float x) {
		int index = Arrays.binarySearch(pointXs, x);
		
		if (index >= 0) {
			// Wow, exact match, me surprised
			return pointYs[index];
		}
		
		int bigger = -index - 1;
		int smaller = bigger - 1;
		
		if (smaller == -1) {
			return pointYs[bigger] + (x - pointXs[bigger]) * slopeAtNegInf;
		} else if (bigger == pointXs.length) {
			return pointYs[smaller] + (x - pointXs[smaller]) * slopeAtPosInf;
		} else {
			float t = (x - pointXs[smaller]) / (pointXs[bigger] - pointXs[smaller]);
			return pointYs[smaller] * (1 - t) + pointYs[bigger] * t;
		}
	}

}
