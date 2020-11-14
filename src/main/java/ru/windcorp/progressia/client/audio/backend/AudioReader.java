package ru.windcorp.progressia.client.audio.backend;

import org.lwjgl.BufferUtils;
import ru.windcorp.progressia.client.audio.backend.SoundType;
import ru.windcorp.progressia.common.resource.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.openal.AL10.*;

public class AudioReader {
	
	private AudioReader() {}
	
	// TODO fix converting from mono-stereo
	// TODO change audio naming from full path to just name
	private static SoundType readAsSpecified(String path, String audioNamespace,
											 String audioName, int format) {
		IntBuffer channelBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer rateBuffer = BufferUtils.createIntBuffer(1);
		
		Resource res = ResourceManager.getResource(path);
		
		ShortBuffer rawAudio = decodeVorbis(res, channelBuffer, rateBuffer);
		
		return new SoundType(audioNamespace ,audioName, rawAudio, format,
				rateBuffer.get(0));
	}
	
	public static SoundType readAsMono(String path, String audioNamespace,
									   String audioName) {
		return readAsSpecified(path, audioNamespace,
				audioName, AL_FORMAT_MONO16);
	}
	
	public static SoundType readAsStereo(String path,String audioNamespace,
										 String audioName) {
		return readAsSpecified(path, audioNamespace, audioName, AL_FORMAT_STEREO16);
	}
	
	private static ShortBuffer decodeVorbis(
		Resource dataToDecode,
		IntBuffer channelsBuffer,
		IntBuffer rateBuffer
	) {
		return stb_vorbis_decode_memory(
			dataToDecode.readAsBytes(),
			channelsBuffer,
			rateBuffer
		);
	}
}
