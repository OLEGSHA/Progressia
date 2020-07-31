package ru.windcorp.optica.common.util;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author <a href="https://stackoverflow.com/users/37416/mike-houston">Mike
 *         Houston</a>, adapted by Javapony
 */
public class ByteBufferInputStream extends InputStream {

	private final ByteBuffer buffer;

	public ByteBufferInputStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public int read() {
		if (!buffer.hasRemaining()) {
			return -1;
		}
		return buffer.get() & 0xFF;
	}

	public int read(byte[] bytes, int off, int len) {
		if (!buffer.hasRemaining()) {
			return -1;
		}

		len = Math.min(len, buffer.remaining());
		buffer.get(bytes, off, len);
		return len;
	}

}