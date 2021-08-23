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

package ru.windcorp.progressia.client.audio.backend;

import org.lwjgl.BufferUtils;
import ru.windcorp.progressia.common.resource.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.openal.AL10.*;

public class AudioReader {

	private AudioReader() {
	}

	// TODO fix converting from mono-stereo
	private static SoundType readAsSpecified(Resource resource, String id, int format) {
		IntBuffer channelBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer rateBuffer = BufferUtils.createIntBuffer(1);

		ShortBuffer rawAudio = decodeVorbis(resource, channelBuffer, rateBuffer);

		return new SoundType(id, rawAudio, format, rateBuffer.get(0));
	}

	public static SoundType readAsMono(Resource resource, String id) {
		return readAsSpecified(resource, id, AL_FORMAT_MONO16);
	}

	public static SoundType readAsStereo(Resource resource, String id) {
		return readAsSpecified(resource, id, AL_FORMAT_STEREO16);
	}

	private static ShortBuffer decodeVorbis(Resource dataToDecode, IntBuffer channelsBuffer, IntBuffer rateBuffer) {
		return stb_vorbis_decode_memory(dataToDecode.readAsBytes(), channelsBuffer, rateBuffer);
	}
}
