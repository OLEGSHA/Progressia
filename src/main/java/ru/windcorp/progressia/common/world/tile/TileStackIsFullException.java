package ru.windcorp.progressia.common.world.tile;

public class TileStackIsFullException extends RuntimeException {

	private static final long serialVersionUID = 6665942370305610231L;

	public TileStackIsFullException() {
		
	}

	public TileStackIsFullException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TileStackIsFullException(String message, Throwable cause) {
		super(message, cause);
	}

	public TileStackIsFullException(String message) {
		super(message);
	}

	public TileStackIsFullException(Throwable cause) {
		super(cause);
	}
	
}
