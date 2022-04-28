import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
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
        String optionName = "-" + option.value();
        OptionParser<?> parser = null;
        if (optionType == int.class) {
            parser = new SingleValueOptionParser<>(Integer::parseInt);
        }
        if (optionType == String.class) {
            parser = new SingleValueOptionParser<>(Function.identity());
        }
        if (optionType == boolean.class) {
            parser = new BoolOptionParser();
        }
        return Optional.ofNullable(parser)
                .map(p -> p.parse(args, optionName))
                .orElseThrow(UnsupportedOperationException::new);
    }

}
