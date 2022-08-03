package exception;

import java.io.Serial;

public class IncorrectCloudNetworkOfferException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -4893235745426480172L;

	public IncorrectCloudNetworkOfferException() {
		super("The received Cloud Network offer has incorrect structure");
	}
}
