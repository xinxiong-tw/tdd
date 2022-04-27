import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;

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

    // single int
    @Test
    public void should_return_8080_while_give_port() {
        IntOption option = Args.parse(IntOption.class, "-p", "8080");
        assertEquals(option.port(), 8080);
    }

    record BoolOptions(@Option("l") boolean logging) {
    }

    record BoolOptionV(@Option("v") boolean isVerbose) {
    }

    record IntOption(@Option("p") int port) {
    }

    private static class Args {
        public static <T> T parse(Class<T> optionsClass, String... args) {
            Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];
            Boolean[] params = Arrays.stream(constructor.getParameters()).map(parameter -> {
                Option option = parameter.getAnnotation(Option.class);
                String optionName = option.value();
                return !Arrays.stream(args).filter(arg -> arg.equals("-" + optionName)).findFirst().map(String::isEmpty).orElse(true);
            }).toArray(Boolean[]::new);
            try {
                return (T) constructor.newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
