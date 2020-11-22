package ru.windcorp.progressia.common.world;

public class IllegalCoordinatesException extends RuntimeException {

	private static final long serialVersionUID = 1362481281554206710L;

	public IllegalCoordinatesException() {
		super();
	}

	public IllegalCoordinatesException(String message) {
		super(message);
	}

	public IllegalCoordinatesException(Throwable cause) {
		super(cause);
	}

	public IllegalCoordinatesException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalCoordinatesException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
