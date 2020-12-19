package ru.windcorp.progressia.common.world;

/**
 * Thrown to indicate that some data could not be properly decoded.
 * @author javapony
 */
public class DecodingException extends Exception {

	private static final long serialVersionUID = 3200709153311801198L;

	public DecodingException() {
		super();
	}

	public DecodingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DecodingException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecodingException(String message) {
		super(message);
	}

	public DecodingException(Throwable cause) {
		super(cause);
	}

}
