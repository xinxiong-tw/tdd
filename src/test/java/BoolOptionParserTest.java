import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoolOptionParserTest {
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

    @Test
    public void should_throw_error_while_get_value() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(BoolOptions.class, "-l", "p"));
    }

    record BoolOptions(@Option("l") boolean logging) {
    }

    record BoolOptionV(@Option("v") boolean isVerbose) {
    }
}