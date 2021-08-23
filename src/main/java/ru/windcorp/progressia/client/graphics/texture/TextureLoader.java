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

package ru.windcorp.progressia.client.graphics.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.util.BinUtil;

public class TextureLoader {

	public static TextureDataEditor loadPixels(InputStream compressed, TextureSettings settings) throws IOException {
		BufferedImage readResult = ImageIO.read(compressed);

		int width = readResult.getWidth();
		int height = readResult.getHeight();

		int bufferWidth = BinUtil.roundToGreaterPowerOf2(width);
		int bufferHeight = BinUtil.roundToGreaterPowerOf2(height);

		WritableRaster raster = TextureUtil.createRaster(bufferWidth, bufferHeight);

		BufferedImage canvas = TextureUtil.createCanvas(raster);

		Graphics2D g = canvas.createGraphics();

		try {
			g.setColor(TextureUtil.CANVAS_BACKGROUND);
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			g.drawImage(readResult, 0, 0, width, height, 0, height, width, 0, // Flip
																				// the
																				// image
					null);
		} finally {
			g.dispose();
		}

		TextureDataEditor result = new TextureDataEditor(bufferWidth, bufferHeight, width, height, settings);

		result.draw(TextureUtil.extractBytes(raster), bufferWidth, 0, 0, 0, 0, width, height);

		return result;
	}

	public static TextureDataEditor loadPixels(Resource resource, TextureSettings settings) throws IOException {
		return loadPixels(resource.getInputStream(), settings);
	}

	private TextureLoader() {
	}

}
