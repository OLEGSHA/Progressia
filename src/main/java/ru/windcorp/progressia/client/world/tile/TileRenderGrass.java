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
 
package ru.windcorp.progressia.client.world.tile;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerCube.OpaqueSurface;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TileRenderGrass extends TileRender implements OpaqueSurface {

	private final Texture topTexture;
	private final Texture sideTexture;

	public TileRenderGrass(
		String id,
		Texture top,
		Texture side
	) {
		super(id);
		this.topTexture = top;
		this.sideTexture = side;
	}

	@Override
	public Texture getTexture(BlockFace face) {
		return (face == BlockFace.TOP) ? topTexture : sideTexture;
	}

	@Override
	public boolean isOpaque(BlockFace face) {
		return face == BlockFace.TOP;
	}

	@Override
	public Renderable createRenderable(BlockFace face) {
		ShapeRenderProgram program = WorldRenderProgram.getDefault();

		return new Shape(
			Usage.STATIC,
			WorldRenderProgram.getDefault(),
			Faces.createBlockFace(
				program,
				getTexture(face),
				Colors.WHITE,
				new Vec3(0, 0, 0),
				face,
				false
			)
		);
	}

	@Override
	public boolean needsOwnRenderable() {
		return false;
	}

}
