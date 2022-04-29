import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class OptionParserTest {

    @Nested
    class UnaryParserTest {
        private final OptionParser<Integer> intOptionParser = OptionParser.unary(0, Integer::parseInt);

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

    @Nested
    class BoolParserTest {
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

    @Nested
    class ListOptionTest {
        // -g "this" "is" {"this", "is"}
        @Test
        public void should_return_list_while_has_two_values() {
            OptionParser<String[]> parser = OptionParser.list(String[]::new, Function.identity());
            String[] values = parser.parse(List.of("-g", "this", "is"), "-g");
            assertArrayEquals(values, new String[]{"this", "is"});
        }
    }

}