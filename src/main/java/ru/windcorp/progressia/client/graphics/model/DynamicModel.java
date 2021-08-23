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

package ru.windcorp.progressia.client.graphics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.primitives.Booleans;

import glm.mat._4.Mat4;

public abstract class DynamicModel extends Model {

	private static final Mat4 IDENTITY = new Mat4();

	private final Mat4[] transforms;
	private final boolean[] dynamics;

	public DynamicModel(Renderable[] parts, Mat4[] transforms, boolean[] dynamic) {
		super(parts);
		this.transforms = transforms;
		this.dynamics = dynamic;
	}

	public DynamicModel(Builder builder) {
		this(builder.getParts(), builder.getTransforms(), builder.getDynamics());
	}

	@Override
	protected Mat4 getTransform(int shapeIndex) {
		Mat4 transform = transforms[shapeIndex];

		if (dynamics[shapeIndex]) {
			transform.identity();
			getDynamicTransform(shapeIndex, transform);
		}

		return transform;
	}

	protected abstract void getDynamicTransform(int shapeIndex, Mat4 result);

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final List<Renderable> parts = new ArrayList<>();
		private final List<Mat4> transforms = new ArrayList<>();
		private final List<Boolean> dynamics = new ArrayList<>();

		protected Builder() {
		}

		private Builder addPart(Renderable part, Mat4 transform, boolean isDynamic) {
			parts.add(Objects.requireNonNull(part, "part"));
			transforms.add(Objects.requireNonNull(transform, "transform"));
			dynamics.add(isDynamic);

			return this;
		}

		public Builder addStaticPart(Renderable part, Mat4 transform) {
			return addPart(part, new Mat4(transform), false);
		}

		public Builder addDynamicPart(Renderable part) {
			return addPart(part, new Mat4(), true);
		}

		public Builder addStaticPart(Renderable part) {
			return addStaticPart(part, IDENTITY);
		}

		private Renderable[] getParts() {
			return parts.toArray(new Renderable[parts.size()]);
		}

		private Mat4[] getTransforms() {
			return transforms.toArray(new Mat4[transforms.size()]);
		}

		private boolean[] getDynamics() {
			return Booleans.toArray(dynamics);
		}

	}

}
