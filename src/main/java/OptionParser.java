import java.util.List;
import java.util.Optional;
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

    static Optional<List<String>> getAndCheckValueCount(List<String> arguments, String optionName, int valueCount) {
        int optionIndex = arguments.indexOf(optionName);
        if (optionIndex == -1) {
            return Optional.empty();
        }
        List<String> optionRawValues = OptionParser.getOptionRawValues(arguments, optionIndex + 1);
        if (optionRawValues.size() < valueCount) {
            throw new IllegalArgumentException(optionName + " expect to get " + valueCount + " value");
        }
        if (optionRawValues.size() > valueCount) {
            throw new IllegalArgumentException(optionName + " expect to get " + valueCount + " value");
        }
        return Optional.of(optionRawValues);
    }

    T parse(List<String> arguments, String optionName);
}
