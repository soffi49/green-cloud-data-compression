package exception;

public class IncorrectTaskDateException extends RuntimeException{

    private static final long serialVersionUID = -3780619229831674878L;

    public IncorrectTaskDateException() {
        super("The provided execution date has incorrect format");
    }
}
