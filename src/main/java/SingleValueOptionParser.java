import java.util.List;
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
        int optionIndex = arguments.indexOf(optionName);
        if (optionIndex == -1) {
            return defaultValue;
        }
        List<String> optionRawValues = OptionParser.getAndCheckValueCount(arguments, optionName, 1);
        return parser.apply(optionRawValues.get(0));
    }

}
