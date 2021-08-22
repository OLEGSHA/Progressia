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
package ru.windcorp.progressia.common.world.generic.context;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.StashingStack;
import ru.windcorp.progressia.common.world.generic.BlockGeneric;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.generic.TileGeneric;
import ru.windcorp.progressia.common.world.rels.RelFace;

//@formatter:off
public abstract class AbstractContextRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	E  extends EntityGeneric
> implements TileGenericContextRO<B, T, E> {
//@formatter:on

	public static final int MAX_SUBCONTEXTS = 64;

	protected class Frame {

		public final Vec3i location = new Vec3i();
		public RelFace face;
		public int layer;

		@Override
		public String toString() {
			return "Frame [x=" + location.x + ", y=" + location.y + ", z=" + location.z + ", face=" + face + ", layer="
				+ layer + "]";
		}

	}

	protected Frame frame = null;

	private final StashingStack<Frame> frameStack = new StashingStack<>(MAX_SUBCONTEXTS, Frame::new);

	@Override
	public void pop() {
		if (!isSubcontexting()) {
			throw new IllegalStateException("Cannot pop(): already top frame");
		}

		frameStack.pop();
		frame = frameStack.peek();
	}

	@Override
	public BlockGenericContextRO<B, T, E> push(Vec3i location) {
		frame = frameStack.push();

		frame.location.set(location.x, location.y, location.z);
		frame.face = null;
		frame.layer = -1;

		return this;
	}

	@Override
	public TileStackGenericContextRO<B, T, E> push(Vec3i location, RelFace face) {
		frame = frameStack.push();

		frame.location.set(location.x, location.y, location.z);
		frame.face = face;
		frame.layer = -1;

		return this;
	}

	@Override
	public TileGenericContextRO<B, T, E> push(Vec3i location, RelFace face, int layer) {
		frame = frameStack.push();

		frame.location.set(location.x, location.y, location.z);
		frame.face = face;
		frame.layer = layer;

		return this;
	}

	public boolean isSubcontexting() {
		return !frameStack.isEmpty();
	}

}
