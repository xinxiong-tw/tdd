import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

class Args {
    public static <T> T parse(Class<T> optionsClass, String... args) {
        Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];
        Object[] params = Arrays.stream(constructor.getParameters()).map(parameter -> parseOption(parameter, List.of(args))).toArray();
        try {
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseOption(Parameter parameter, List<String> args) {
        Class<?> optionType = parameter.getType();
        Option option = parameter.getAnnotation(Option.class);
        if (option == null) {
            throw new IllegalArgumentException(parameter.getName() + " must have a option annotation");
        }
        String optionName = "-" + option.value();
        OptionParser<?> parser = getOptionParser(optionType);
        return Optional.ofNullable(parser)
                .map(p -> p.parse(args, optionName))
                .orElseThrow(UnsupportedOperationException::new);
    }

    private static final Map<Class<?>, OptionParser<?>> PARSERS = Map.of(
            int.class, OptionParser.unary(0, Integer::parseInt),
            String.class, OptionParser.unary("", Function.identity()),
            boolean.class, OptionParser.bool()
    );

    private static OptionParser<?> getOptionParser(Class<?> optionType) {
        return PARSERS.get(optionType);
    }

}
