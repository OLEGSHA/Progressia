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

import static ru.windcorp.progressia.client.graphics.texture.TextureUtil.BYTES_PER_PIXEL;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class TextureDataEditor {

	protected final TextureData data;

	public TextureDataEditor(int bufferWidth, int bufferHeight, int contentWidth, int contentHeight,
			TextureSettings settings) {
		this.data = new TextureData(BufferUtils.createByteBuffer(bufferWidth * bufferHeight * 4), bufferWidth,
				bufferHeight, contentWidth, contentHeight, settings);
	}

	public TextureDataEditor(TextureData data) {
		this.data = data;
	}

	public TextureData getData() {
		return data;
	}

	protected ByteBuffer getBuffer() {
		return getData().getData();
	}

	public TextureData createSnapshot() {
		TextureData t = getData();

		ByteBuffer fromBuffer = getBuffer();
		ByteBuffer toBuffer = BufferUtils.createByteBuffer(fromBuffer.capacity());

		copy(fromBuffer, 0, fromBuffer.capacity(), toBuffer);
		toBuffer.clear();

		return new TextureData(toBuffer, t.getBufferWidth(), t.getBufferHeight(), t.getContentWidth(),
				t.getContentHeight(), t.getSettings());
	}

	public TextureData createSnapshot(TextureData output) {
		ByteBuffer src = getBuffer();
		ByteBuffer dst = output.getData();

		int position = dst.position();
		int limit = dst.limit();

		try {
			copy(src, 0, src.capacity(), output.getData());
		} finally {
			dst.limit(dst.capacity()).position(position).limit(limit);
		}

		return output;
	}

	public int getBufferWidth() {
		return getData().getBufferWidth();
	}

	public int getBufferHeight() {
		return getData().getBufferHeight();
	}

	public int getContentWidth() {
		return getData().getContentWidth();
	}

	public int getContentHeight() {
		return getData().getContentHeight();
	}

	public TextureSettings getSettings() {
		return getData().getSettings();
	}

	public void draw(ByteBuffer src, int srcWidth, int srcX, int srcY, int dstX, int dstY, int width, int height) {
		ByteBuffer dst = getBuffer();

		int position = src.position();
		int limit = src.limit();

		try {

			for (int line = 0; line < height; ++line) {
				src.limit(src.capacity());

				position(dst, dstX, dstY + line, getBufferWidth());
				position(src, srcX, srcY + line, srcWidth);
				setLength(src, width);

				dst.put(src);

				dst.clear();
			}

		} finally {
			src.limit(src.capacity()).position(position).limit(limit);
		}
	}

	public void draw(TextureData source, int srcX, int srcY, int dstX, int dstY, int width, int height) {
		draw(source.getData(), source.getBufferWidth(), srcX, srcY, dstX, dstY, width, height);
	}

	public void draw(TextureData source, int dstX, int dstY) {
		draw(source, 0, 0, dstX, dstY, source.getContentWidth(), source.getContentHeight());
	}

	public void draw(TextureDataEditor source, int srcX, int srcY, int dstX, int dstY, int width, int height) {
		draw(source.getData(), srcX, srcY, dstX, dstY, width, height);
	}

	public void draw(TextureDataEditor source, int dstX, int dstY) {
		draw(source, 0, 0, dstX, dstY, source.getContentWidth(), source.getContentHeight());
	}

	public void setPixel(int x, int y, int color) {
		ByteBuffer dst = getBuffer();

		position(dst, x, y, getBufferWidth());
		dst.putInt(color);
		dst.clear();
	}

	private static void position(ByteBuffer buffer, int x, int y, int width) {
		buffer.position((y * width + x) * BYTES_PER_PIXEL);
	}

	private static void setLength(ByteBuffer buffer, int length) {
		buffer.limit(buffer.position() + length * BYTES_PER_PIXEL);
	}

	private static void copy(ByteBuffer src, int srcStart, int srcEnd, ByteBuffer dst) {
		int position = src.position();
		int limit = src.limit();

		try {
			src.limit(src.capacity()).position(srcStart).limit(srcEnd);
			dst.put(src);
		} finally {
			src.limit(src.capacity()).position(position).limit(limit);
		}
	}

}
