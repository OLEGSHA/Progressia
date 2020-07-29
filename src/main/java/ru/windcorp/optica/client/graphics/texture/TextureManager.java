/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.graphics.texture;

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import javax.imageio.ImageIO;

import ru.windcorp.optica.client.graphics.backend.RenderTaskQueue;
import ru.windcorp.optica.common.resource.Resource;
import ru.windcorp.optica.common.resource.ResourceManager;

public class TextureManager {
	
	private static final ColorModel COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // Use RGB
			new int[] {8, 8, 8, 8},                     // Use every bit
			true,                                       // Has alpha
			false,                                      // Not premultiplied
			ComponentColorModel.TRANSLUCENT,            // Can have any alpha
			DataBuffer.TYPE_BYTE                        // Alpha is one byte
	);
	
	private static final Hashtable<?, ?> CANVAS_PROPERTIES = new Hashtable<>();
	private static final java.awt.Color CANVAS_BACKGROUND =
			new java.awt.Color(0, 0, 0, 0);
	
	private static final String TEXTURE_ASSETS_PREFIX = "assets/textures/";
	
	private static Resource getResource(String textureName) {
		return ResourceManager.getResource(
				TEXTURE_ASSETS_PREFIX + textureName + ".png"
		);
	}
	
	public static TexturePrimitive load(String textureName, boolean filtered) {
		TexturePrimitive result = loadToByteBuffer(textureName, filtered);
		RenderTaskQueue.invokeLater(result::load);
		return result;
	}
	
	public static TexturePrimitive loadToByteBuffer(
			String textureName, boolean filter
	) {
		Resource resource = getResource(textureName);
		
		BufferedImage source = readImage(resource);
		
		int bufferWidth = toPowerOf2(source.getWidth()),
				bufferHeight = toPowerOf2(source.getHeight());
		
		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, // Storage model
				bufferWidth,          // Buffer width
				bufferHeight,         // Buffer height
				4,                    // RGBA
				null                  // Location (here (0; 0))
		);
		
		BufferedImage canvas = new BufferedImage(
				COLOR_MODEL,      // Color model
				raster,           // Backing raster
				false,            // Raster is not premultipied
				CANVAS_PROPERTIES // Properties
		);
		
		Graphics g = canvas.createGraphics();
		g.setColor(CANVAS_BACKGROUND);
		g.fillRect(0, 0, source.getWidth(), source.getHeight());
		g.drawImage(
				source,
				0, 0, source.getWidth(), source.getHeight(),
				0, source.getHeight(), source.getWidth(), 0, // Flip the image
				null
		);
		g.dispose();
		
		byte[] data = (
				(DataBufferByte) canvas.getRaster().getDataBuffer()
		).getData();
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(data);
		buffer.flip();
		
		Pixels pixels = new Pixels(buffer, bufferWidth, bufferHeight, filter);
		
		TexturePrimitive result = new TexturePrimitive(
				pixels,
				source.getWidth(),
				source.getHeight(),
				bufferWidth,
				bufferHeight
		);
		
		return result;
	}
	
	private static BufferedImage readImage(Resource resource) {
		try {
			return ImageIO.read(resource.getInputStream());
		} catch (Exception e) {
			throw new RuntimeException("too bad. refresh project u stupid. must be " + resource.getName(), e);
		}
	}
	
	private static int toPowerOf2(int i) {
		
		// TODO optimize
		
		int result = 1;
		do {
			result *= 2;
		} while (result < i);
		return result;
	}

}