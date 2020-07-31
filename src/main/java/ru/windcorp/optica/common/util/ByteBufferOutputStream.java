package ru.windcorp.optica.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

	private final ByteBuffer buffer;

	public ByteBufferOutputStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public void write(int b) throws IOException {
		try {
			buffer.put((byte) b);
		} catch (BufferOverflowException e) {
			throw new IOException(e);
		}
	}

	public void write(byte[] bytes, int off, int len) throws IOException {
		try {
			buffer.put(bytes, off, len);
		} catch (BufferOverflowException e) {
			throw new IOException(e);
		}
	}

}