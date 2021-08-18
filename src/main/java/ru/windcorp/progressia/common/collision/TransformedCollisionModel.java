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

package ru.windcorp.progressia.common.collision;

import java.util.function.Consumer;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;

public class TransformedCollisionModel implements CollisionModel {

	private final CollisionModel parent;

	private final Mat4 transform = new Mat4();
	private final Mat4 inverseTransform = new Mat4();

	public TransformedCollisionModel(CollisionModel parent, Mat4 transform) {
		this.parent = parent;
		setTransform(transform);
	}

	public TransformedCollisionModel(CollisionModel parent, Consumer<Mat4> transform) {
		this.parent = parent;
		transform.accept(this.transform);
		this.inverseTransform.set(this.transform).inverse();
	}

	public Mat4 getTransform() {
		return transform;
	}

	public void setTransform(Mat4 newTransform) {
		this.transform.set(newTransform);
		this.inverseTransform.set(newTransform).inverse();
	}

	@Override
	public void setOrigin(Vec3 origin) {
		Vec3 vec3 = Vectors.grab3();
		VectorUtil.applyMat4(origin, inverseTransform, vec3);
		this.parent.setOrigin(vec3);
		Vectors.release(vec3);
	}

	@Override
	public void moveOrigin(Vec3 displacement) {
		Vec3 vec3 = Vectors.grab3();
		VectorUtil.applyMat4(displacement, inverseTransform, vec3);
		this.parent.moveOrigin(vec3);
		Vectors.release(vec3);
	}

}
