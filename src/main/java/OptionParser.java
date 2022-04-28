import java.util.List;
import java.util.stream.IntStream;

public interface OptionParser<T> {

    private static boolean isOption(String arg) {
        return arg.startsWith("-");
    }

    static List<String> getOptionRawValues(List<String> arguments, int valueIndex) {
        return arguments.subList(valueIndex, IntStream.range(valueIndex, arguments.size())
                .filter(index -> isOption(arguments.get(index)))
                .findFirst()
                .orElse(arguments.size()));
    }

    T parse(List<String> arguments, String optionName);
}
