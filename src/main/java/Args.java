import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

class Args<T> {

    private final Map<Class<?>, OptionParser<?>> parsers;
    private final Class<T> optionsClass;

    public Args(Map<Class<?>, OptionParser<?>> parsers, Class<T> optionClass) {
        this.parsers = parsers;
        this.optionsClass = optionClass;
    }

    public Args(Class<T> optionsClass) {
        this(PARSERS, optionsClass);
    }

    public T parse(String... args) {
        Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];
        Object[] params = Arrays.stream(constructor.getParameters())
                .map(parameter -> parseOption(parameter, List.of(args)))
                .toArray();
        try {
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<Class<?>, OptionParser<?>> PARSERS = Map.of(
            int.class, OptionParser.unary(0, Integer::parseInt),
            String.class, OptionParser.unary("", Function.identity()),
            boolean.class, OptionParser.bool(),
            String[].class, OptionParser.list(String[]::new, Function.identity()),
            Integer[].class, OptionParser.list(Integer[]::new, Integer::parseInt)
    );

    private Object parseOption(Parameter parameter, List<String> args) {
        Class<?> optionType = parameter.getType();
        Option option = parameter.getAnnotation(Option.class);
        if (option == null) {
            throw new IllegalArgumentException(parameter.getName() + " must have a option annotation");
        }
        String optionName = "-" + option.value();
        OptionParser<?> parser = getOptionParser(optionType, parsers);
        return Optional.ofNullable(parser)
                .map(p -> p.parse(args, optionName))
                .orElseThrow(UnsupportedOperationException::new);
    }

    private OptionParser<?> getOptionParser(Class<?> optionType, Map<Class<?>, OptionParser<?>> parsers) {
        return parsers.get(optionType);
    }

}
