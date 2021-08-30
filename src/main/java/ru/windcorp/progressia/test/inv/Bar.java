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
package ru.windcorp.progressia.test.inv;

import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.jputil.functions.FloatSupplier;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeParts;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;

public class Bar extends Component {
	
	private static final int THICKNESS = 5;

	private final boolean isVertical;

	private final FloatSupplier value;
	private final FloatSupplier maxValue;
	
	private final Vec4 color;
	private final Vec4 backgroundColor;

	private static Renderable unitSquare = null;

	public Bar(String name, boolean isVertical, Vec4 color, FloatSupplier value, FloatSupplier maxValue) {
		super(name);
		this.isVertical = isVertical;
		this.value = value;
		this.maxValue = maxValue;
		
		this.color = color;
		this.backgroundColor = Colors.mix(color, Colors.WHITE, 0.75f, null);

		if (unitSquare == null) {
			unitSquare = new Shape(
				Usage.STATIC,
				FlatRenderProgram.getDefault(),
				ShapeParts.createRectangle(
					FlatRenderProgram.getDefault(),
					null,
					Colors.WHITE,
					new Vec3(0, 0, 0),
					new Vec3(1, 0, 0),
					new Vec3(0, 1, 0),
					false
				)
			);
		}

		setPreferredSize(THICKNESS, THICKNESS);
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		target.addCustomRenderer(this::renderSelf);
	}

	private void renderSelf(ShapeRenderHelper renderer) {
		renderer.pushTransform()
			.translate(getX(), getY(), 0)
			.scale(getWidth(), getHeight(), 1);
		
		float length = value.getAsFloat() / maxValue.getAsFloat();
		if (length < 0) {
			length = 0;
		} else if (length > 1) {
			length = 1;
		}
		
		// TODO why is the order reverse????
		renderRectangle(renderer, color, length);
		renderRectangle(renderer, backgroundColor, 1);
		
		renderer.popTransform();
	}
	
	private void renderRectangle(ShapeRenderHelper renderer, Vec4 color, float length) {
		renderer.pushColorMultiplier().mul(color);
		if (length != 1) {
			renderer.pushTransform().scale(isVertical ? 1 : length, isVertical ? length : 1, 1);
		}
		
		unitSquare.render(renderer);
		
		if (length != 1) {
			renderer.popTransform();
		}
		renderer.popColorMultiplier();
	}

}
