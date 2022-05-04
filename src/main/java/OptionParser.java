import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface OptionParser<T> {
    static String[] checkCount(String optionName, String[] optionValues, int count) {
        if (optionValues.length < count) {
            throw new TooLessArgumentException(optionName);
        }
        if (optionValues.length > count) {
            throw new TooManyArgumentsException(optionName);
        }
        return optionValues;
    }

    static <T> OptionParser<T> unary(Function<String, T> parser, T defaultValue) {
        return (optionName, optionValues) -> Optional.ofNullable(optionValues)
                .map(values -> OptionParser.checkCount(optionName, values, 1))
                .map(values -> values[0])
                .map(parser)
                .orElse(defaultValue);
    }

    static <T> OptionParser<T[]> list(Function<String, T> parser, IntFunction<T[]> generator) {
        return (optionName, optionValues) -> Optional.ofNullable(optionValues)
                .map(values -> Arrays.stream(values)
                        .map(parser)
                        .toArray(generator))
                .orElse(generator.apply(0));
    }

    static OptionParser<Boolean> bool() {
        return ((optionName, optionValues) -> Optional.ofNullable(optionValues)
                .map(values -> OptionParser.checkCount(optionName, values, 0))
                .isPresent());
    }

    static OptionParser<Map<String, String>> map() {
        return ((optionName, optionValues) -> Optional.ofNullable(optionValues)
                .map(values -> {
                    if (values.length == 0) {
                        throw new TooLessArgumentException(optionName);
                    }
                    return values;
                })
                .map(values -> {
                    HashMap<String, String> hashMap = new HashMap<>();
                    for (String optionValue : values) {
                        String[] split = optionValue.split("=", 2);
                        String key = split[0];
                        String value = split[1];
                        hashMap.put(key, value);
                    }
                    return hashMap;
                })
                .orElse(new HashMap<>()));
    }

    T parse(String optionName, String[] optionValues);
}
