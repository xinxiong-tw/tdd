public class TooLessArgumentException extends RuntimeException {

    String argument;
    public TooLessArgumentException(String argument) {
        this.argument = argument;
    }
}
