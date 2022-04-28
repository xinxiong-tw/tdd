import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

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
        int valueIndex = optionIndex + 1;
        List<String> optionRawValues = getOptionRawValues(arguments, valueIndex);
        if (optionRawValues.size() < 1) {
            throw new IllegalArgumentException(optionName + "expect to get a value");
        }
        if (optionRawValues.size() > 1) {
            throw new IllegalArgumentException(optionName + "expect single value");
        }
        return parser.apply(optionRawValues.get(0));
    }

    private List<String> getOptionRawValues(List<String> arguments, int valueIndex) {
        return arguments.subList(valueIndex, IntStream.range(valueIndex, arguments.size())
                .filter(index -> OptionParser.isOption(arguments.get(index)))
                .findFirst()
                .orElse(arguments.size()));
    }
}
