package runner.exception;

public class InvalidScenarioException extends RuntimeException {

	public InvalidScenarioException(String message, Exception e) {
		super(message, e);
	}
}
