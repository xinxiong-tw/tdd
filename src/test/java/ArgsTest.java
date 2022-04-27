import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    @Test
    public void should_return_true_while_l_is_present() {
        BoolOptions options = Args.parse(BoolOptions.class, "-l");
        Assertions.assertTrue(options.logging());
    }

    @Test
    public void should_return_false_while_l_is_not_present() {
        BoolOptions options = Args.parse(BoolOptions.class, "");
        Assertions.assertFalse(options.logging());
    }

    record BoolOptions(@Option("l") boolean logging) {
    }

    private static class Args {
        public static BoolOptions parse(Class<BoolOptions> optionsClass, String... args) {
            boolean logging = Arrays.asList(args).contains("-l");
            return new BoolOptions(logging);
        }
    }
}
