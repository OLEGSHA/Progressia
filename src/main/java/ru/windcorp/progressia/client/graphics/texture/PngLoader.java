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
package ru.windcorp.progressia.client.graphics.texture;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

import ru.windcorp.progressia.common.util.BinUtil;

class PngLoader {
	
	private static final ColorModel COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // Use RGB
			new int[] {8, 8, 8, 8},                     // Use every bit
			true,                                       // Has alpha
			false,                                      // Not premultiplied
			ComponentColorModel.TRANSLUCENT,            // Can have any alpha
			DataBuffer.TYPE_BYTE                        // Alpha is one byte
	);
	
	private static final Hashtable<?, ?> CANVAS_PROPERTIES = new Hashtable<>();
	
	private static final java.awt.Color CANVAS_BACKGROUND = new java.awt.Color(0, 0, 0, 0);
	
	public static Pixels loadPngImage(
			InputStream pngStream,
			TextureSettings settings
	) throws IOException {

		BufferedImage readResult = ImageIO.read(pngStream);
		
		int width = readResult.getWidth();
		int height = readResult.getHeight();
		
		int bufferWidth = BinUtil.roundToGreaterPowerOf2(width);
		int bufferHeight = BinUtil.roundToGreaterPowerOf2(height);
		
		WritableRaster raster = createRaster(bufferWidth, bufferHeight);

		BufferedImage canvas = createCanvas(raster);
		
		Graphics2D g = canvas.createGraphics();
		try {
			g.setColor(CANVAS_BACKGROUND);
			g.fillRect(0, 0, width, height);
			g.drawImage(
					readResult,
					0, 0, width, height,
					0, height, width, 0, // Flip the image
					null
			);
		} finally {
			g.dispose();
		}
		
		return new Pixels(
				extractBytes(raster),
				bufferWidth, bufferHeight,
				width, height,
				settings
		);
	}
	
	private static WritableRaster createRaster(
			int bufferWidth,
			int bufferHeight
	) {
		return Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, // Storage model
				bufferWidth,          // Buffer width
				bufferHeight,         // Buffer height
				4,                    // RGBA
				null                  // Location (here (0; 0))
		);
	}
	
	private static BufferedImage createCanvas(WritableRaster raster) {
		return new BufferedImage(
				COLOR_MODEL,      // Color model
				raster,           // Backing raster
				false,            // Raster is not premultipied
				CANVAS_PROPERTIES // Properties
		);
	}
	
	private static ByteBuffer extractBytes(WritableRaster raster) {
		byte[] data = (
				(DataBufferByte) raster.getDataBuffer()
		).getData();
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}

}
