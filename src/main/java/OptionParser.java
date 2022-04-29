import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public interface OptionParser<T> {

    private static boolean isOption(String arg) {
        return arg.startsWith("-");
    }

    private static List<String> getOptionRawValues(List<String> arguments, int valueIndex) {
        return arguments.subList(valueIndex, IntStream.range(valueIndex, arguments.size())
                .filter(index -> isOption(arguments.get(index)))
                .findFirst()
                .orElse(arguments.size()));
    }

    private static Optional<List<String>> getRawValues(List<String> arguments, String optionName) {
        int optionIndex = arguments.indexOf(optionName);
        if (optionIndex == -1) {
            return Optional.empty();
        }
        List<String> optionRawValues = getOptionRawValues(arguments, optionIndex + 1);
        return Optional.of(optionRawValues);
    }

    private static List<String> checkValueCount(String optionName, int valueCount, List<String> optionRawValues) {
        if (optionRawValues.size() < valueCount) {
            throw new IllegalArgumentException(optionName + " expect to get " + valueCount + " value");
        }
        if (optionRawValues.size() > valueCount) {
            throw new IllegalArgumentException(optionName + " expect to get " + valueCount + " value");
        }
        return optionRawValues;
    }

    static OptionParser<Boolean> bool() {
        return (arguments, optionName) -> getRawValues(arguments, optionName)
                .map(values -> checkValueCount(optionName, 0, values))
                .isPresent();
    }

    static <T> OptionParser<T> unary(T defaultValue, Function<String, T> parser) {
        return (arguments, optionName) -> getRawValues(arguments, optionName)
                .map(values -> checkValueCount(optionName, 1, values))
                .map(values -> parser.apply(values.get(0)))
                .orElse(defaultValue);
    }

    static <T> OptionParser<T[]> list(IntFunction<T[]> generator, Function<String, T> parser) {
        return ((arguments, optionName) -> getRawValues(arguments, optionName)
                .map(values -> values.stream()
                        .map(parser)
                        .toArray(generator)
                ).orElse(generator.apply(0)));
    }

    T parse(List<String> arguments, String optionName);
}
