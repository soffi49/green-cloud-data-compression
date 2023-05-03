package runner.exception;

public class JadeControllerException extends RuntimeException {

	public JadeControllerException(String message, Exception exception) {
		super(message, exception);
	}
}
