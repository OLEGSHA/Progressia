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

package ru.windcorp.progressia.client.graphics.model;

import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;

public class FaceGroup {

	private final TexturePrimitive texture;
	private final int indexCount;
	private final int byteOffsetOfIndices;

	FaceGroup(Face[] faces, int start, int end) {

		Texture t = faces[start].getTexture();
		this.texture = t == null ? null : t.getSprite().getPrimitive();
		this.byteOffsetOfIndices = faces[start].getByteOffsetOfIndices();

		int indexCount = 0;

		for (int i = start; i < end; ++i) {
			Face face = faces[i];

			assert this.texture == null ? (face.getTexture() == null)
					: (face.getTexture().getSprite().getPrimitive() == this.texture);

			indexCount += face.getIndexCount();
		}

		this.indexCount = indexCount;
	}

	public TexturePrimitive getTexture() {
		return this.texture;
	}

	public int getIndexCount() {
		return this.indexCount;
	}

	public int getByteOffsetOfIndices() {
		return this.byteOffsetOfIndices;
	}

}
