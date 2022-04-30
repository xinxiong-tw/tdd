public class TooManyArgumentsException extends RuntimeException {
    String argument;

    public TooManyArgumentsException(String argument, String message, Throwable error) {
        super(message, error);
        this.argument = argument;
    }
}
