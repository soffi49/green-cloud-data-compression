package exception;

import java.io.Serial;

public class IncorrectJobStructureException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 6301764250517553188L;

    public IncorrectJobStructureException() {
        super("The received job has incorrect structure");
    }
}
