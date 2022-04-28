import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class SingleValueOptionParser<T> implements OptionParser<T> {

    private final Function<String, T> parser;
    private final T defaultValue;

    public SingleValueOptionParser(T defaultValue, Function<String, T> parser) {
        this.parser = parser;
        this.defaultValue = defaultValue;
    }

    @Override
    public T parse(List<String> arguments, String optionName) {
        return OptionParser.getAndCheckValueCount(arguments, optionName, 1)
                .map(values -> parser.apply(values.get(0)))
                .orElse(defaultValue);
    }

}
