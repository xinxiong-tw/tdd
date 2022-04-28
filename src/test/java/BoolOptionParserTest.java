import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoolOptionParserTest {

    private final OptionParser<Boolean> parser = OptionParser.bool();

    // single bool
    @Test
    public void should_return_true_while_l_is_present() {
        assertTrue(parser.parse(List.of("-l"), "-l"));
    }

    @Test
    public void should_return_false_while_l_is_not_present() {
        assertFalse(parser.parse(List.of(), "-l"));
    }

    @Test
    public void should_return_false_while_name_is_not_l_and_not_present() {
        assertFalse(parser.parse(List.of(), "-v"));
    }

    @Test
    public void should_throw_error_while_get_value() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of("-l", "p"), "-l"));
    }

}