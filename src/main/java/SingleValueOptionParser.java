import java.util.function.Function;

public class SingleValueOptionParser<T> implements OptionParser<T> {

    Function<String, T> parser;

    public SingleValueOptionParser(Function<String, T> parser) {
        this.parser = parser;
    }

    @Override
    public T parse(String[] optionValues) {
        String optionValue = optionValues[0];
        return parser.apply(optionValue);
    }
}
