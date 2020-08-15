package ru.windcorp.progressia.client.graphics.texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import glm.vec._2.Vec2;
import ru.windcorp.progressia.client.graphics.backend.RenderTaskQueue;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.util.BinUtil;
import ru.windcorp.progressia.common.util.Named;

public class Atlases {
	
	public static class AtlasGroup extends Named {
		private final int atlasSize;
		
		public AtlasGroup(String name, int atlasSize) {
			super(name);
			this.atlasSize = atlasSize;
			
			if (!BinUtil.isPowerOf2(atlasSize)) {
				throw new IllegalArgumentException(
						"Atlas size " + atlasSize
						+ " is not a power of 2"
				);
			}
		}
		
		public int getAtlasSize() {
			return atlasSize;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
	}
	
	public static class Atlas {
		private final AtlasGroup group;
		
		private final TextureDataEditor editor;
		private int nextX, nextY;
		private int rowHeight;
		
		private final TexturePrimitive primitive;
		
		public Atlas(AtlasGroup group) {
			this.group = group;
			int size = group.getAtlasSize();
			
			this.editor = new TextureDataEditor(size, size, size, size, SETTINGS);
			this.primitive = new TexturePrimitive(editor.createSnapshot());
		}
		
		public Sprite addSprite(TextureDataEditor data) {
			int width = data.getContentWidth();
			int height = data.getContentHeight();
			
			selectPosition(width, height);
			
			editor.draw(data, nextX, nextY);
			
			Sprite result = new Sprite(
					getPrimitive(),
					toPrimitiveCoords(nextX, nextY),
					toPrimitiveCoords(width, height)
			);
			
			nextX += width;
			
			return result;
		}

		private void selectPosition(int width, int height) {
			if (nextX + width > getSize()) {
				// Wrapping
				nextY += rowHeight; // Move to next row
				rowHeight = height; // Next row is at least 'height' high
				nextX = 0;          // Start the row over
			} else {
				// Not wrapping
				
				// Update rowHeight if necessary
				if (rowHeight < height) {
					rowHeight = height;
				}
			}
		}

		private Vec2 toPrimitiveCoords(int x, int y) {
			return new Vec2(
					toPrimitiveCoord(x),
					toPrimitiveCoord(y)
			);
		}

		private float toPrimitiveCoord(int c) {
			return c / (float) getSize();
		}

		public boolean canAddSprite(TextureDataEditor data) {
			int width = data.getContentWidth();
			int height = data.getContentHeight();
			
			// Try to fit without wrapping
			
			if (nextY + height > getSize())
				// Does not fit vertically
				return false;
			
			if (nextX + width <= getSize())
				// Can place at (nextX; nextY)
				return true;
			
			// Try wrapping
			
			if (width > getSize())
				// GTFO. We couldn't fit if if we tried
				return false;
			
			if (nextY + rowHeight + height > getSize())
				// Does not fit vertically when wrapped
				return false;
			
			// Can place at (0; nextY + rowHeight)
			return true;
		}
		
		public void flush() {
			editor.createSnapshot(primitive.getData());
			editor.close();
		}
		
		public AtlasGroup getGroup() {
			return group;
		}
		
		public TexturePrimitive getPrimitive() {
			return primitive;
		}
		
		public int getSize() {
			return editor.getBufferWidth();
		}
	}
	
	private static final TextureSettings SETTINGS = new TextureSettings(false);
	
	private static final Map<Resource, Sprite> LOADED =
			new HashMap<>();
	private static final Multimap<AtlasGroup, Atlas> ATLASES =
			MultimapBuilder.hashKeys().arrayListValues().build();
	
	public static Sprite getSprite(Resource resource, AtlasGroup group) {
		return LOADED.computeIfAbsent(resource, k -> loadSprite(k, group));
	}
	
	private static Sprite loadSprite(Resource resource, AtlasGroup group) {
		try (
				TextureDataEditor data =
						TextureLoader.loadPixels(resource, SETTINGS)
		) {
			
			Atlas atlas = getReadyAtlas(group, data);
			return atlas.addSprite(data);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Atlas getReadyAtlas(AtlasGroup group, TextureDataEditor data) {
		List<Atlas> atlases = (List<Atlas>) ATLASES.get(group);
		
		if (
				atlases.isEmpty() ||
				!(atlases.get(atlases.size() - 1).canAddSprite(data))
		) {
			Atlas newAtlas = new Atlas(group);
			
			if (!newAtlas.canAddSprite(data)) {
				throw new RuntimeException("Could not fit tex into atlas of size " + newAtlas.getSize());
			}
			
			atlases.add(newAtlas);
		}
		
		return atlases.get(atlases.size() - 1);
	}

	public static void loadAllAtlases() {
		ATLASES.forEach((group, atlas) -> {
			atlas.flush();
			RenderTaskQueue.invokeLater(atlas.getPrimitive()::load);
		});
	}
	
	private Atlases() {}

}
