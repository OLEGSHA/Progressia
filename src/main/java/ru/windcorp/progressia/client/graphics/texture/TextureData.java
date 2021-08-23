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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.nio.ByteBuffer;

class TextureData {

	private final ByteBuffer data;

	private final int bufferWidth;
	private final int bufferHeight;

	private final TextureSettings settings;

	private final int width;
	private final int height;

	public TextureData(ByteBuffer data, int bufferWidth, int bufferHeight, int width, int height,
			TextureSettings settings) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.bufferWidth = bufferWidth;
		this.bufferHeight = bufferHeight;
		this.settings = settings;
	}

	public int load() {
		int handle = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, handle);

		if (settings.isFiltered()) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		}

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexImage2D(GL_TEXTURE_2D, // Load 2D image
				0, // Not mipmapped
				GL_RGBA, // Use RGBA
				bufferWidth, // Width
				bufferHeight, // Height
				0, // No border
				GL_RGBA, // Use RGBA (required)
				GL_UNSIGNED_BYTE, // Use unsigned bytes
				data // Data buffer
		);

		return handle;
	}

	public ByteBuffer getData() {
		return data;
	}

	public int getBufferWidth() {
		return bufferWidth;
	}

	public int getBufferHeight() {
		return bufferHeight;
	}

	public int getContentWidth() {
		return width;
	}

	public int getContentHeight() {
		return height;
	}

	public TextureSettings getSettings() {
		return settings;
	}

}
