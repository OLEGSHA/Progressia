package ru.windcorp.progressia.common.util.namespaces;

public class IllegalIdException extends RuntimeException {

	private static final long serialVersionUID = -1572240191058305981L;

	public IllegalIdException() {
		super();
	}

	protected IllegalIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalIdException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalIdException(String message) {
		super(message);
	}

	public IllegalIdException(Throwable cause) {
		super(cause);
	}

}
