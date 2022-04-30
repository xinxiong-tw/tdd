public class TooManyArgumentsException extends RuntimeException {
    String argument;

    public TooManyArgumentsException(String argument) {
        this.argument = argument;
    }
}
