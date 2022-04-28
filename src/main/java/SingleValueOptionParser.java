import java.util.List;
import java.util.function.Function;

class SingleValueOptionParser<T> implements OptionParser<T> {

    private final Function<String, T> parser;

    public SingleValueOptionParser(Function<String, T> parser) {
        this.parser = parser;
    }

    private static boolean isValue(List<String> args, int valueIndex) {
        return args.get(valueIndex).startsWith("-");
    }

    @Override
    public T parse(List<String> arguments, String optionName) {
        int optionIndex = arguments.indexOf(optionName);
        int valueIndex = optionIndex + 1;
        if (valueIndex >= arguments.size() || isValue(arguments, valueIndex)) {
            throw new IllegalArgumentException(optionName + "expect to get a value");
        }
        int nextOptionIndex = optionIndex + 2;
        if (nextOptionIndex < arguments.size() && !isValue(arguments, nextOptionIndex)) {
            throw new IllegalArgumentException(optionName + "expect single value");
        }
        return parser.apply(arguments.get(valueIndex));
    }
}
