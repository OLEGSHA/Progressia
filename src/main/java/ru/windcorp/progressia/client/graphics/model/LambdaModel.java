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

public class LambdaModel extends DynamicModel {

	private static final Mat4 IDENTITY = new Mat4();

	@FunctionalInterface
	public static interface TransformGetter {
		void transform(Mat4 result);
	}

	private final TransformGetter[] getters;

	public LambdaModel(Renderable[] parts, Mat4[] transforms, boolean[] dynamic, TransformGetter[] getters) {
		super(parts, transforms, dynamic);
		this.getters = getters;
	}

	public LambdaModel(Builder builder) {
		this(builder.getParts(), builder.getTransforms(), builder.getDynamics(), builder.getGetters());
	}

	@Override
	protected void getDynamicTransform(int shapeIndex, Mat4 result) {
		getters[shapeIndex].transform(result);
	}

	public static Builder lambdaBuilder() {
		return new Builder();
	}

	public static class Builder {

		private final List<Renderable> parts = new ArrayList<>();
		private final List<Mat4> transforms = new ArrayList<>();
		private final List<Boolean> dynamics = new ArrayList<>();
		private final List<TransformGetter> getters = new ArrayList<>();

		protected Builder() {
		}

		private Builder addPart(Renderable part, Mat4 transform, TransformGetter getter) {
			parts.add(Objects.requireNonNull(part, "part"));
			transforms.add(Objects.requireNonNull(transform, "transform"));
			dynamics.add(getter != null);
			getters.add(getter);

			return this;
		}

		public Builder addStaticPart(Renderable part, Mat4 transform) {
			return addPart(part, new Mat4(transform), null);
		}

		public Builder addDynamicPart(Renderable part, TransformGetter getter) {
			return addPart(part, new Mat4(), getter);
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

		private TransformGetter[] getGetters() {
			return getters.toArray(new TransformGetter[getters.size()]);
		}

	}

	public static LambdaModel animate(Renderable model, TransformGetter transform) {
		return new LambdaModel(new Renderable[] { model }, new Mat4[] { new Mat4() }, new boolean[] { true },
				new TransformGetter[] { transform });
	}

}
