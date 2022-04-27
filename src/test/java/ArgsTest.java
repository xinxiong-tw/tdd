import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    // single bool
    @Test
    public void should_return_true_while_l_is_present() {
        BoolOptions options = Args.parse(BoolOptions.class, "-l");
        assertTrue(options.logging());
    }

    @Test
    public void should_return_false_while_l_is_not_present() {
        BoolOptions options = Args.parse(BoolOptions.class, "");
        assertFalse(options.logging());
    }

    @Test
    public void should_return_false_while_name_is_not_l_and_not_present() {
        BoolOptionV options = Args.parse(BoolOptionV.class, "");
        assertFalse(options.isVerbose());
    }

    record BoolOptions(@Option("l") boolean logging) {
    }

    record BoolOptionV(@Option("v") boolean isVerbose) {
    }

    // single int
    @Test
    public void should_return_8080_while_give_port() {
        IntOption option = Args.parse(IntOption.class, "-p", "8080");
        assertEquals(option.port(), 8080);
    }

    @Test
    public void should_throw_error_while_no_value() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(IntOption.class, "-p"));
    }

    @Test
    public void should_throw_error_while_no_value_2() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(IntOption.class, "-p", "-l"));
    }

    @Test
    public void should_throw_error_while_extra_value() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(IntOption.class, "-p", "8080", "8888"));
    }

    record IntOption(@Option("p") int port) {
    }

    // single string
    @Test
    public void should_return_log_file_path_while_give_directory() {
        StringOption option = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals(option.directory(), "/usr/logs");
    }

    @Test
    public void should_throw_error_while_no_value_for_directory() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(StringOption.class, "-d", "-p", "8080"));
    }

    @Test
    public void should_throw_error_while_no_value_for_directory_2() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(StringOption.class, "-d", "-l"));
    }

    record StringOption(@Option("d") String directory) {
    }

    private static class Args {
        public static <T> T parse(Class<T> optionsClass, String ...args) {
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
            if (optionType == int.class) {
                return parseSingleValueWith(args, optionName, Integer::parseInt);
            }
            if (optionType == String.class) {
                return parseSingleValueWith(args, optionName, Function.identity());
            }
            if (optionType == boolean.class) {
                return args.contains(optionName);
            }
            throw new UnsupportedOperationException("unsupported for type " + optionType);
        }

        private static <T> T parseSingleValueWith(List<String> args, String optionName, Function<String, T> transfer) {
            int optionIndex = args.indexOf(optionName);
            if ((optionIndex + 1) >= args.size() || args.get(optionIndex + 1).startsWith("-")) {
                throw new IllegalArgumentException(optionName + "expect to get a value");
            }
            if ((optionIndex + 2) < args.size() && !args.get(optionIndex + 2).startsWith("-")) {
                throw new IllegalArgumentException(optionName + "expect single value");
            }
            return transfer.apply(args.get(optionIndex + 1));
        }

    }
}
