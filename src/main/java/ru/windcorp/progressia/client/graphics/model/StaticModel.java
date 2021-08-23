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

import glm.mat._4.Mat4;

public class StaticModel extends Model {

	private static final Mat4 IDENTITY = new Mat4();

	private final Mat4[] transforms;

	public StaticModel(Renderable[] parts, Mat4[] transforms) {
		super(parts);
		this.transforms = transforms;
	}

	@Override
	protected Mat4 getTransform(int partIndex) {
		return transforms[partIndex];
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final List<Renderable> parts = new ArrayList<>();
		private final List<Mat4> transforms = new ArrayList<>();

		protected Builder() {
		}

		public Builder addPart(Renderable part, Mat4 transform) {
			parts.add(Objects.requireNonNull(part, "part"));
			transforms.add(Objects.requireNonNull(transform, "transform"));

			return this;
		}

		public Builder addPart(Renderable part) {
			return addPart(part, IDENTITY);
		}

		private Renderable[] getParts() {
			return parts.toArray(new Renderable[parts.size()]);
		}

		private Mat4[] getTransforms() {
			return transforms.toArray(new Mat4[transforms.size()]);
		}

		public StaticModel build() {
			return new StaticModel(getParts(), getTransforms());
		}

	}

}
