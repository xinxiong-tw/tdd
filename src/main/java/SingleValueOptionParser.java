import java.util.Optional;
import java.util.function.Function;

public class SingleValueOptionParser<T> implements OptionParser<T> {

    Function<String, T> parser;
    private final T defaultValue;

    public SingleValueOptionParser(Function<String, T> parser, T defaultValue) {
        this.parser = parser;
        this.defaultValue = defaultValue;
    }

    @Override
    public T parse(String optionName, String[] optionValues) {
        return Optional.ofNullable(optionValues)
                .map(values -> checkCount(optionName, values))
                .map(this::getValue)
                .map(value -> parser.apply(value))
                .orElse(defaultValue);
    }

    private String getValue(String[] values) {
        return values[0];
    }

    private String[] checkCount(String optionName, String[] optionValues) {
        if (optionValues.length != 1) {
            throw new TooManyArgumentsException(optionName);
        }
        return optionValues;
    }
}
