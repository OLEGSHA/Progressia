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
			
			assert this.texture == null
					? (face.getTexture() == null)
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
