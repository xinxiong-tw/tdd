import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SingleValueOptionParserTest {

    private final SingleValueOptionParser<Integer> intOptionParser = new SingleValueOptionParser<>(Integer::parseInt);

    // single value
    @Test
    public void should_return_8080_while_give_port() {
        assertEquals(intOptionParser.parse(List.of("-p", "8080"), "-p"), 8080);
    }

    @Test
    public void should_throw_error_while_no_value() {
        assertThrows(IllegalArgumentException.class, () -> intOptionParser.parse(List.of("-p"), "-p"));
    }

    @Test
    public void should_throw_error_while_no_value_2() {
        assertThrows(IllegalArgumentException.class, () -> intOptionParser.parse(List.of("-p", "-l"), "-p"));
    }

    @Test
    public void should_throw_error_while_extra_value() {
        assertThrows(IllegalArgumentException.class, () -> intOptionParser.parse(List.of("-p", "8080", "8888"), "-p"));
    }
}