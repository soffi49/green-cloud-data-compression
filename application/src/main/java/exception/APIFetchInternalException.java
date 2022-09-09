package exception;

import java.io.Serial;

public class APIFetchInternalException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = 5295657283189216960L;

	public APIFetchInternalException() {
		super("The API retrieved null instead of the weather data");
	}
}
