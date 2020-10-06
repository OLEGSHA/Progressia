package ru.windcorp.progressia.common.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.trove.list.array.TByteArrayList;

public class DataBuffer {
	
	private static final int DEFAULT_CAPACITY = 1024;
	private static final int TRANSFER_BUFFER_SIZE = 1024;

	private final TByteArrayList buffer;
	
	private final byte[] transferBuffer = new byte[TRANSFER_BUFFER_SIZE];
	
	private int position;
	
	private final DataInput reader = new DataInputStream(
			new InputStream() {
				@Override
				public int read() throws IOException {
					if (position >= buffer.size()) return -1;
					int result = buffer.getQuick(position);
					++position;
					return result;
				}
			}
	);
	
	private final DataOutput writer = new DataOutputStream(
			new OutputStream() {		
				@Override
				public void write(int b) throws IOException {
					buffer.add((byte) b);
				}
			}
	);
	
	public DataBuffer(int capacity) {
		this.buffer = new TByteArrayList(capacity);
	}
	
	public DataBuffer() {
		this(DEFAULT_CAPACITY);
	}
	
	public DataBuffer(DataBuffer copyFrom) {
		this.buffer = new TByteArrayList(copyFrom.buffer);
	}
	
	public DataInput getReader() {
		position = 0;
		return reader;
	}
	
	public DataOutput getWriter() {
		buffer.resetQuick();
		return writer;
	}

	public int getSize() {
		return buffer.size();
	}
	
	public void fill(DataInput source, int length) throws IOException {
		buffer.resetQuick();
		buffer.ensureCapacity(length);
		
		while (length > 0) {
			int currentLength = Math.min(transferBuffer.length, length);
			
			source.readFully(transferBuffer, 0, currentLength);
			buffer.add(transferBuffer, 0, currentLength);
			
			length -= currentLength;
		}
	}
	
	public void flush(DataOutput sink) throws IOException {
		int position = 0;
		int length = buffer.size();
		
		while (position < length) {
			int currentLength = Math.min(
					transferBuffer.length,
					length - position
			);
			
			buffer.toArray(transferBuffer, position, 0, currentLength);
			sink.write(transferBuffer, 0, currentLength);
			
			length -= currentLength;
		}
	}

	@Override
	public int hashCode() {
		return buffer.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataBuffer other = (DataBuffer) obj;
		if (!buffer.equals(other.buffer))
			return false;
		return true;
	}

}
