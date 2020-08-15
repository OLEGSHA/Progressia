package ru.windcorp.progressia.client.graphics.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TextureDataEditor implements AutoCloseable {
	
	protected final BufferedImage image;
	protected Graphics2D graphics;
	
	private final int contentWidth;
	private final int contentHeight;
	private final TextureSettings settings;
	
	protected TextureDataEditor(
			BufferedImage image,
			int contentWidth, int contentHeight,
			TextureSettings settings
	) {
		this.image = image;
		this.contentWidth = contentWidth;
		this.contentHeight = contentHeight;
		this.settings = settings;
		
		startEditing();
	}
	
	public TextureDataEditor(
			int bufferWidth, int bufferHeight,
			int contentWidth, int contentHeight,
			TextureSettings settings
	) {
		this(
				TextureUtils.createCanvas(
						TextureUtils.createRaster(bufferWidth, bufferHeight)
				),
				contentWidth, contentHeight,
				settings
		);
	}
	
	public TextureDataEditor(TextureData buffer) {
		this(
			createImage(buffer),
			buffer.getContentWidth(),
			buffer.getContentHeight(),
			buffer.getSettings()
		);
	}

	private static BufferedImage createImage(TextureData buffer) {
		return TextureUtils.createCanvas(
				TextureUtils.createRaster(
						buffer.getData(),
						buffer.getBufferWidth(),
						buffer.getBufferHeight()
				)
		);
	}

	public void startEditing() {
		if (isEditable()) {
			throw new IllegalStateException("This object is already editable");
		}
		
		this.graphics = this.image.createGraphics();
		
		graphics.setColor(TextureUtils.CANVAS_BACKGROUND);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		graphics.clipRect(0, 0, contentWidth, contentHeight);
	}
	
	public void finishEditing() {
		checkEditability();
		this.graphics.dispose();
		this.graphics = null;
	}
	
	@Override
	public void close() {
		if (isEditable()) {
			finishEditing();
		}
	}
	
	public boolean isEditable() {
		return this.graphics != null;
	}
	
	protected void checkEditability() {
		if (!isEditable()) {
			throw new IllegalStateException("This object is not editable");
		}
	}
	
	public TextureData createSnapshot() {
		return new TextureData(
				TextureUtils.extractBytes(image.getRaster()),
				image.getWidth(), image.getHeight(),
				contentWidth, contentHeight,
				settings
		);
	}
	
	public TextureData createSnapshot(TextureData output) {
		TextureUtils.extractBytes(image.getRaster(), output.getData());
		return output;
	}
	
	public TextureData toStatic() {
		close();
		return createSnapshot();
	}

	public TextureData toStatic(TextureData data) {
		close();
		return createSnapshot(data);
	}

	public int getBufferWidth() {
		return image.getWidth();
	}
	
	public int getBufferHeight() {
		return image.getHeight();
	}
	
	public int getContentWidth() {
		return contentWidth;
	}
	
	public int getContentHeight() {
		return contentHeight;
	}
	
	public TextureSettings getSettings() {
		return settings;
	}
	
	public void draw(
			BufferedImage source,
			int srcX, int srcY,
			int dstX, int dstY,
			int width, int height
	) {
		checkEditability();
		
		graphics.drawImage(
				source,
				dstX, dstY,
				dstX + width, dstY + height,
				srcX, srcY,
				srcX + width, srcY + height,
				null
		);
	}
	
	public void draw(
			BufferedImage source,
			int dstX, int dstY
	) {
		draw(source, 0, 0, dstX, dstY, source.getWidth(), source.getHeight());
	}
	
	public void draw(
			TextureDataEditor source,
			int srcX, int srcY,
			int dstX, int dstY,
			int width, int height
	) {
		draw(source.image, srcX, srcY, dstX, dstY, width, height);
	}
	
	public void draw(
			TextureDataEditor source,
			int dstX, int dstY
	) {
		draw(
				source, 0, 0, dstX, dstY,
				source.getContentWidth(), source.getContentHeight()
		);
	}

}
