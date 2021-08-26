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
package ru.windcorp.progressia.client.world.item;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeParts;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.world.item.ItemData;

public class ItemRenderSimple extends ItemRender {

	private Texture texture;
	private Renderable renderable;

	public ItemRenderSimple(String id, Texture texture) {
		super(id);
		this.texture = texture;
		
		this.renderable = new Shape(
			Usage.STATIC, 
			FlatRenderProgram.getDefault(),
			ShapeParts.createRectangle(
				FlatRenderProgram.getDefault(),
				texture,
				Colors.WHITE,
				new Vec3(0, 0, 0),
				new Vec3(24, 0, 0),
				new Vec3(0, 24, 0),
				false
			)
		);
	}
	
	public Texture getTexture() {
		return texture;
	}

	@Override
	public ItemRenderable createRenderable(ItemData data) {
		return new ItemRenderable(data) {
			@Override
			protected void doRender(ShapeRenderHelper renderer) {
				renderable.render(renderer);
			}
		};
	}

}
