package exception;

import java.io.Serial;

public class IncorrectServerOfferException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -8817130563697757507L;

    public IncorrectServerOfferException() {
        super("The received Server offer has incorrect structure");
    }
}
