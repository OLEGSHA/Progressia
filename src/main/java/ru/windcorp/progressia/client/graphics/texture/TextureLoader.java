package ru.windcorp.progressia.client.graphics.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.util.BinUtil;

public class TextureLoader {
	
	public static TextureDataEditor loadPixels(
			InputStream compressed, TextureSettings settings
	) throws IOException {
		BufferedImage readResult = ImageIO.read(compressed);
		
		int width = readResult.getWidth();
		int height = readResult.getHeight();
		
		int bufferWidth = BinUtil.roundToGreaterPowerOf2(width);
		int bufferHeight = BinUtil.roundToGreaterPowerOf2(height);
		
		TextureDataEditor result = new TextureDataEditor(
				bufferWidth, bufferHeight, width, height, settings
		);
		
		Graphics2D g = result.graphics;
		g.drawImage(
				readResult,
				0, 0, width, height,
				0, height, width, 0, // Flip the image
				null
		);
		
		return result;
	}
	
	public static TextureDataEditor loadPixels(
			Resource resource, TextureSettings settings
	) throws IOException {
		return loadPixels(resource.getInputStream(), settings);
	}
	
	private TextureLoader() {}

}
