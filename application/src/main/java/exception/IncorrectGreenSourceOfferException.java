package exception;

import java.io.Serial;

public class IncorrectGreenSourceOfferException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -3924437496940745912L;

    public IncorrectGreenSourceOfferException() {
        super("The received Green Source offer has incorrect structure");
    }
}
