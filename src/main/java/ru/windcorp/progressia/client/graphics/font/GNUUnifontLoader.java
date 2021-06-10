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

package ru.windcorp.progressia.client.graphics.font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.texture.Atlases.AtlasGroup;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Sprite;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.texture.TextureDataEditor;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class GNUUnifontLoader {

	private static final AtlasGroup ATLAS_GROUP_GNU_UNIFONT = new AtlasGroup("GNUUnifont", 1 << 12);

	private static final TextureSettings TEXTURE_SETTINGS = new TextureSettings(false);

	private static final int BITS_PER_HEX_DIGIT = 4;
	private static final int PREFIX_LENGTH = "0000:".length();

	private static class ParsedGlyph {
		final char c;
		final TextureDataEditor data;

		ParsedGlyph(char c, TextureDataEditor data) {
			this.c = c;
			this.data = data;
		}
	}

	private static class AtlasGlyph {
		final char c;
		final Texture texture;

		AtlasGlyph(char c, Texture texture) {
			this.c = c;
			this.texture = texture;
		}
	}

	public static GNUUnifont load(Resource resource) {
		try (BufferedReader reader = createReader(resource)) {
			return createStream(reader).map(GNUUnifontLoader::parse).map(GNUUnifontLoader::addToAtlas)
					.collect(Collectors.collectingAndThen(createMapper(), GNUUnifont::new));
		} catch (IOException | UncheckedIOException e) {
			throw CrashReports.report(e, "Could not load GNUUnifont");
		}
	}

	private static BufferedReader createReader(Resource resource) throws IOException {
		return new BufferedReader(
				new InputStreamReader(new GZIPInputStream(resource.getInputStream()), StandardCharsets.UTF_8));
	}

	private static Stream<String> createStream(BufferedReader reader) {
		return reader.lines();
	}

	private static ParsedGlyph parse(String declar) {
		try {

			int width = getWidth(declar);
			checkDeclaration(declar, width);

			char c = getChar(declar);

			TextureDataEditor editor = new TextureDataEditor(width, GNUUnifont.HEIGHT, width, GNUUnifont.HEIGHT,
					TEXTURE_SETTINGS);

			for (int y = 0; y < GNUUnifont.HEIGHT; ++y) {
				for (int x = 0; x < width; ++x) {
					int bit = x + y * width;

					editor.setPixel(x, GNUUnifont.HEIGHT - y - 1, getBit(declar, bit) ? 0xFFFFFFFF : 0x00000000);
				}
			}

			return new ParsedGlyph(c, editor);

		} catch (IOException e) {
			throw CrashReports.report(e, "Could not load GNUUnifont: could not load character \"%s\"", declar);
		}
	}

	private static char getChar(String declar) {
		int result = 0;

		for (int i = 0; i < 4; ++i) {
			result = (result << BITS_PER_HEX_DIGIT) | getHexValue(declar.charAt(i));
		}

		return (char) result;
	}

	private static boolean getBit(String declar, int bit) {
		int character = PREFIX_LENGTH + (bit / BITS_PER_HEX_DIGIT);
		bit = bit % BITS_PER_HEX_DIGIT;

		char c = declar.charAt(character);
		int value = getHexValue(c);

		return (value & (1 << (BITS_PER_HEX_DIGIT - bit - 1))) != 0;
	}

	private static int getWidth(String declar) {
		int meaningfulChars = declar.length() - PREFIX_LENGTH;
		final int charsPerColumn = GNUUnifont.HEIGHT / BITS_PER_HEX_DIGIT;
		return meaningfulChars / charsPerColumn;
	}

	private static void checkDeclaration(String declar, int width) throws IOException {
		if (!GNUUnifont.WIDTHS.contains(width)) {
			throw new IOException("Width " + width + " is not supported (in declar \"" + declar + "\")");
		}

		if ((declar.length() - PREFIX_LENGTH) % width != 0) {
			throw new IOException("Declar \"" + declar + "\" has invalid length");
		}

		for (int i = 0; i < declar.length(); ++i) {
			if (i == BITS_PER_HEX_DIGIT) {
				if (declar.charAt(i) != ':') {
					throw new IOException("No colon ':' found in declar \"" + declar + "\" at index 4");
				}
			} else {
				char c = declar.charAt(i);

				if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F'))) {
					throw new IOException(
							"Illegal char in declar \"" + declar + "\" at index " + i + "; expected 0-9A-F");
				}
			}
		}
	}

	private static int getHexValue(char digit) {
		if (digit < '0')
			throw new NumberFormatException(digit + " is not a hex digit (0-9A-F expected)");
		if (digit <= '9')
			return digit - '0';
		if (digit < 'A')
			throw new NumberFormatException(digit + " is not a hex digit (0-9A-F expected)");
		if (digit <= 'F')
			return digit - 'A' + 0xA;
		throw new NumberFormatException(digit + " is not a hex digit (0-9A-F expected)");
	}

	private static AtlasGlyph addToAtlas(ParsedGlyph glyph) {
		Sprite sprite = Atlases.loadSprite(glyph.data.getData(), ATLAS_GROUP_GNU_UNIFONT);
		return new AtlasGlyph(glyph.c, new SimpleTexture(sprite));
	}

	private static Collector<AtlasGlyph, ?, TCharObjectMap<Texture>> createMapper() {
		return Collector.of(TCharObjectHashMap<Texture>::new,

				(map, glyph) -> map.put(glyph.c, glyph.texture),

				(a, b) -> {
					a.putAll(b);
					return a;
				},

				Characteristics.UNORDERED);
	}

	private GNUUnifontLoader() {
	}

}
