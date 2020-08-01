package ru.windcorp.optica.client.graphics.flat;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.optica.client.graphics.Colors;
import ru.windcorp.optica.client.graphics.backend.GraphicsInterface;
import ru.windcorp.optica.client.graphics.input.KeyEvent;
import ru.windcorp.optica.client.graphics.texture.SimpleTexture;
import ru.windcorp.optica.client.graphics.texture.Sprite;
import ru.windcorp.optica.client.graphics.texture.Texture;
import ru.windcorp.optica.client.graphics.texture.TextureManager;
import ru.windcorp.optica.client.graphics.texture.TextureSettings;

public class LayerTestUI extends AssembledFlatLayer {

	public LayerTestUI() {
		super("TestUI");
		
		GraphicsInterface.subscribeToInputEvents(this);
	}
	
	private boolean flag = false;
	
	@Override
	protected void assemble(RenderTarget target) {
		final int width = 512 + 256;
		final int height = 64;
		final int border = 5;
		
		final int boxColor = flag ? 0xEE8888 : 0xEEEE88;
		final int borderColor = flag ? 0xAA4444 : 0xAAAA44;
		final int boxShadowColor = flag ? 0x440000 : 0x444400;
		
		int x = (getWidth() - width) / 2;
		int y = getHeight() - height;
		
		y -= 2*border;

		target.fill(x + border, y + border, width, height, boxShadowColor);
		target.fill(x - 1, y - 1, width + 2, height + 2, boxShadowColor);
		target.fill(x, y, width, height, borderColor);
		target.fill(x + border, y + border, width - 2*border, height - 2*border, boxColor);
		
		final int texShadow = 2;
		final int texSize = height - 4*border;
		
		target.fill(x + 2*border + texShadow, y + 2*border + texShadow, texSize, texSize, Colors.BLACK);
		target.drawTexture(x + 2*border, y + 2*border, texSize, texSize, qtex("compass"));
		
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

	@Subscribe
	public void onKeyEvent(KeyEvent event) {
		if (event.isRepeat() || event.getKey() != GLFW.GLFW_KEY_LEFT_CONTROL) {
			return;
		}
		
		flag = event.isPress();
		markForReassembly();
	}
	
	/*
	 * TMP texture loader
	 */
	
	private static final TextureSettings TEXTURE_SETTINGS =
			new TextureSettings(false);
	
	private static Texture qtex(String name) {
		return new SimpleTexture(new Sprite(
				TextureManager.load(name, TEXTURE_SETTINGS)
		));
	}

}
