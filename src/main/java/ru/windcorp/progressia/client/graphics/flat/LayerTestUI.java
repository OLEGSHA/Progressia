/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics.flat;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.client.graphics.model.LambdaModel;
import ru.windcorp.progressia.client.graphics.texture.SimpleTextures;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class LayerTestUI extends AssembledFlatLayer {

	public LayerTestUI() {
		super("TestUI");
		
		GraphicsInterface.subscribeToInputEvents(this);
	}
	
	private boolean flag = false;

	private static final int WIDTH = 512 + 256;
	private static final int HEIGHT = 80;
	private static final int BORDER = 5;

	@Override
	protected void assemble(RenderTarget target) {
		final int boxColor = flag ? 0xEE8888 : 0xEEEE88;
		final int borderColor = flag ? 0xAA4444 : 0xAAAA44;
		final int boxShadowColor = flag ? 0x440000 : 0x444400;
		
		int x = (getWidth() - WIDTH) / 2;
		int y = 2*BORDER;

		target.fill(x + BORDER, y - BORDER, WIDTH, HEIGHT, boxShadowColor);
		target.fill(x - 1, y - 1, WIDTH + 2, HEIGHT + 2, boxShadowColor);
		target.fill(x, y, WIDTH, HEIGHT, borderColor);
		target.fill(x + BORDER, y + BORDER, WIDTH - 2*BORDER, HEIGHT - 2*BORDER, boxColor);
		
		final int texShadow = 2;
		final int texSize = HEIGHT - 4*BORDER;
		
		target.pushTransform(new Mat4().identity().translate(x + 2*BORDER, y + 2*BORDER, 0));
		
		final Texture compassBg = SimpleTextures.get("compass_icon");
		final Texture compassFg = SimpleTextures.get("compass_icon_arrow");
		
		target.drawTexture(texShadow, -texShadow, texSize, texSize, Colors.BLACK, compassBg);
		target.drawTexture(0, 0, texSize, texSize, compassBg);
		
		target.addCustomRenderer(new LambdaModel(LambdaModel.lambdaBuilder()
				.addDynamicPart(
						target.createRectagle(0, 0, texSize, texSize, 0xFFFFFF, compassFg),
						mat -> mat.translate(texSize/2, texSize/2, 0).rotateZ(ClientState.getInstance().getCamera().getYaw()).translate(-texSize/2, -texSize/2, 0)
				)
		));
		target.popTransform();
		
		drawCross(target);
	}

	private void drawCross(RenderTarget target) {
		int cx = getWidth() / 2;
		int cy = getHeight() / 2;
		
		final int length = 15;
		final int thickness = 5;
		final int borderSize = 1;
		final int borderColor = Colors.BLACK;
		final int fillColor = Colors.WHITE;
		
		target.fill(
				cx - length - thickness / 2,
				cy - thickness / 2,
				2*length + thickness,
				thickness,
				borderColor
		);
		
		target.fill(
				cx - thickness / 2,
				cy - length - thickness / 2,
				thickness,
				2*length + thickness,
				borderColor
		);
		
		target.fill(
				cx - length - thickness / 2 + borderSize,
				cy - thickness / 2 + borderSize,
				2*length + thickness - 2*borderSize,
				thickness - 2*borderSize,
				fillColor
		);
		
		target.fill(
				cx - thickness / 2 + borderSize,
				cy - length - thickness / 2 + borderSize,
				thickness - 2*borderSize,
				2*length + thickness - 2*borderSize,
				fillColor
		);
	}
	
	@Override
	protected void handleInput(Input input) {
		// TODO Auto-generated method stub
		
	}

	@Subscribe
	public void onKeyEvent(KeyEvent event) {
		if (event.isRepeat() || event.getKey() != GLFW.GLFW_KEY_LEFT_CONTROL) {
			return;
		}
		
		flag = event.isPress();
		invalidate();
	}

}
