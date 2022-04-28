import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SingleValueOptionParserTest {

    private final OptionParser<Integer> intOptionParser = OptionParser.singleParse(0, Integer::parseInt);

    // single value
    @Test
    public void should_return_8080_while_give_port() {
        assertEquals(intOptionParser.parse(List.of("-p", "8080"), "-p"), 8080);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-p -l", "-p"})
    public void should_throw_error_while_no_value(String arguments) {
        assertThrows(IllegalArgumentException.class, () -> intOptionParser.parse(List.of(arguments.split(" ")), "-p"));
    }

    @Test
    public void should_throw_error_while_no_value_2() {
        assertThrows(IllegalArgumentException.class, () -> intOptionParser.parse(List.of("-p", "-l"), "-p"));
    }

    @Test
    public void should_throw_error_while_extra_value() {
        assertThrows(IllegalArgumentException.class, () -> intOptionParser.parse(List.of("-p", "8080", "8888"), "-p"));
    }

    @Test
    public void should_return_default_value_while_option_is_not_present() {
        assertEquals(intOptionParser.parse(List.of(), "-p"), 0);
    }
}