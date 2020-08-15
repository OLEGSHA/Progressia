package ru.windcorp.progressia.client.graphics.texture;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import org.lwjgl.BufferUtils;

public class TextureUtils {
	
	public static final Color CANVAS_BACKGROUND = new Color(0, true);
	
	public static final ColorModel COLOR_MODEL = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB), // Use RGB
			new int[] {8, 8, 8, 8},                     // Use every bit
			true,                                       // Has alpha
			false,                                      // Not premultiplied
			Transparency.TRANSLUCENT,                   // Can have any alpha
			DataBuffer.TYPE_BYTE                        // Alpha is one byte
	);
	
	private static final Hashtable<?, ?> BUFFERED_IMAGE_PROPERTIES =
			new Hashtable<>();
	
	public static WritableRaster createRaster(
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
	
	public static WritableRaster createRaster(
			ByteBuffer buffer,
			int bufferWidth,
			int bufferHeight
	) {
		final int bands = 4; // RGBA
		
		byte[] bytes = new byte[bufferWidth * bufferHeight * bands];
		
		buffer.get(bytes);
		buffer.position(buffer.position() - bytes.length);
		
		DataBufferByte dataBuffer = new DataBufferByte(bytes, bytes.length);
		
		return Raster.createInterleavedRaster(
				dataBuffer,             // The buffer
				bufferWidth,            // Buffer width
				bufferHeight,           // Buffer height
				bands * bufferWidth,    // Scanline stride
				bands,                  // Pixel stride
				new int[] {0, 1, 2, 3}, // Band offsets
				null                    // Location (here (0; 0))
		);
	}
	
	public static BufferedImage createCanvas(WritableRaster raster) {
		return new BufferedImage(
				COLOR_MODEL,              // Color model
				raster,                   // Backing raster
				false,                    // Raster is not premultipied
				BUFFERED_IMAGE_PROPERTIES // Properties
		);
	}
	
	public static ByteBuffer extractBytes(WritableRaster raster) {
		byte[] data = (
				(DataBufferByte) raster.getDataBuffer()
		).getData();
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}
	
	public static ByteBuffer extractBytes(
			WritableRaster raster, ByteBuffer output
	) {
		byte[] data = (
				(DataBufferByte) raster.getDataBuffer()
		).getData();
		
		output.put(data);
		output.flip();
		
		return output;
	}
	
	private TextureUtils() {}

}
