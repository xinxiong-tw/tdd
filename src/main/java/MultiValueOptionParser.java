import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;

public class MultiValueOptionParser<T> implements OptionParser<T[]> {

    Function<String, T> parser;
    IntFunction<T[]> generator;

    public MultiValueOptionParser(Function<String, T> parser, IntFunction<T[]> generator) {
        this.parser = parser;
        this.generator = generator;
    }

    @Override
    public T[] parse(String optionName, String[] optionValues) {
        return Optional.ofNullable(optionValues)
                .map(values -> Arrays.stream(values)
                        .map(parser)
                        .toArray(generator)).orElse(generator.apply(0));
    }
}
