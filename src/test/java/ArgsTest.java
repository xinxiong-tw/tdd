import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

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

    record IntOption(@Option("p") int port) {
    }

    // single string
    @Test
    public void should_return_log_file_path_while_give_directory() {
        StringOption option = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals(option.directory(), "/usr/logs");
    }

    record StringOption(@Option("d") String directory) {
    }

    private static class Args {
        public static <T> T parse(Class<T> optionsClass, String... args) {
            Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];
            Object[] params = Arrays.stream(constructor.getParameters()).map(parameter -> {
                Option option = parameter.getAnnotation(Option.class);
                String optionName = "-" + option.value();
                Class<?> optionType = parameter.getType();
                int optionIndex = Arrays.stream(args).toList().indexOf(optionName);
                if (optionType == int.class) {
                    if ((optionIndex + 1) >= args.length) {
                        throw new IllegalArgumentException(optionName + "expect to get a value");
                    }
                    String optionValue = args[optionIndex + 1];
                    return Integer.parseInt(optionValue);
                }
                if (optionType == boolean.class) {
                    return optionIndex != -1;
                }
                throw new UnsupportedOperationException("unsupported for type " + optionType);
            }).toArray();
            try {
                return (T) constructor.newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
