import java.util.List;
import java.util.function.Function;

class SingleValueOptionParser<T> implements OptionParser<T> {

    private final Function<String, T> parser;

    public SingleValueOptionParser(Function<String, T> parser) {
        this.parser = parser;
    }

    @Override
    public T parse(List<String> arguments, String optionName) {
        int optionIndex = arguments.indexOf(optionName);
        int valueIndex = optionIndex + 1;
        if (valueIndex >= arguments.size() || OptionParser.isValue(arguments, valueIndex)) {
            throw new IllegalArgumentException(optionName + "expect to get a value");
        }
        int nextOptionIndex = optionIndex + 2;
        if (nextOptionIndex < arguments.size() && !OptionParser.isValue(arguments, nextOptionIndex)) {
            throw new IllegalArgumentException(optionName + "expect single value");
        }
        return parser.apply(arguments.get(valueIndex));
    }
}
