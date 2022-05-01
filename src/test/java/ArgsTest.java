import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs

    @Nested()
    class toMapTest {
        @Test
        public void should_return_map_after_parse_arguments() {

            Map<String, String[]> map = Args.toMap(List.of("-p", "8080", "-l", "-d", "/usr/log"));

            assertArrayEquals(map.get("p"), new String[] {"8080"});
            assertArrayEquals(map.get("l"), new String[] {});
            assertArrayEquals(map.get("d"), new String[] {"/usr/log"});
        }

        @Test
        public void should_return_map_with_list_option_after_parse_list_arguments() {
            Map<String, String[]> map = Args.toMap(List.of("-g", "hello", "world"));

            assertArrayEquals(map.get("g"), new String[] {"hello", "world"});
        }
    }


    @Test
    public void should_return_true_after_parse_bool_option() {
        BoolOption option = Args.parse(BoolOption.class, new String[]{"-l"});
        assertTrue(option.logging());
    }

    @Test
    public void should_throw_error_if_too_many_arguments() {
        TooManyArgumentsException tooManyArgumentsException = assertThrows(TooManyArgumentsException.class, () -> Args.parse(BoolOption.class, new String[]{"-l", "p"}));
        assertEquals(tooManyArgumentsException.argument, "l");
    }

    @Test
    public void should_return_false_if_option_not_present() {
        BoolOption option = Args.parse(BoolOption.class, new String[]{""});
        assertFalse(option.logging());
    }

    record BoolOption(@Option("l") boolean logging) {
    }

    @Test
    public void should_return_8080_after_parse_int_option() {
        IntOption option = Args.parse(IntOption.class, new String[]{"-p", "8080"});
        assertEquals(option.port(), 8080);
    }

    @Test
    public void should_throw_too_many_arguments_if_option_has_more_than_one_value() {
        TooManyArgumentsException tooManyArgumentsException =
                assertThrows(TooManyArgumentsException.class,
                        () -> Args.parse(IntOption.class, new String[]{"-p", "8080", "9999"}));
        assertEquals(tooManyArgumentsException.argument, "p");
    }

    @Test
    public void should_return_default_in_value_if_int_option_is_not_present() {
        IntOption option = Args.parse(IntOption.class, new String[]{});
        assertEquals(option.port(), 0);
    }

    record IntOption(@Option("p") int port) {
    }

    @Test
    public void should_return_string_after_parse_string_option() {
        StringOption option = Args.parse(StringOption.class, new String[]{"-d", "/usr/log"});
        assertEquals(option.directory(), "/usr/log");
    }

    @Test
    public void should_throw_too_many_arguments_if_option_has_more_than_one_string_values() {
        TooManyArgumentsException tooManyArgumentsException =
                assertThrows(TooManyArgumentsException.class,
                        () -> Args.parse(StringOption.class, new String[]{"-d", "/usr/log", "/var/log"}));
        assertEquals(tooManyArgumentsException.argument, "d");
    }

    @Test
    public void should_return_default_in_value_if_string_option_is_not_present() {
        StringOption option = Args.parse(StringOption.class, new String[]{});
        assertEquals(option.directory(), "");
    }

    record StringOption(@Option("d") String directory) {
    }

    @Test
    public void should_return_multiple_options_after_parse_arguments() {
        Options option = Args.parse(Options.class, new String[]{"-d", "/usr/log", "-p", "8080", "-l"});
        assertEquals(option.directory(), "/usr/log");
        assertTrue(option.logging());
        assertEquals(option.port(), 8080);
    }
    record Options(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    @Test
    public void should_return_list_string_values_after_parse_list_strings() {
        ListOption option = Args.parse(ListOption.class, new String[]{"-g", "this", "is", "a", "list"});
        assertArrayEquals(new String[]{"this", "is", "a", "list"}, option.groups());
    }

    record ListOption(@Option("g") String[] groups) {
    }

    @Test
    public void should_return_int_list_values_after_parse_int_list() {
        IntListOption option = Args.parse(IntListOption.class, new String[]{"-g", "-1", "0", "1", "10"});
        assertArrayEquals(new Integer[]{-1, 0, 1, 10}, option.numbers());
    }

    record IntListOption(@Option("g") Integer[] numbers) {
    }


}
