import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    @Test
    public void should_parse_multiple_options_one_time() {
        LogOptions options = new Args<>(LogOptions.class).parse("-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.logging());
        assertEquals(options.port(), 8080);
        assertEquals(options.directory, "/usr/logs");
    }

    record LogOptions(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    @Test
    public void should_throw_error_while_no_option_annotation() {
        assertThrows(IllegalArgumentException.class, () -> new Args<>(NoOptionOptions.class).parse("-l"));
    }

    record NoOptionOptions(boolean logging) {
    }


    @Test
    public void should_support_string_list_options() {
        StringListOptions options = new Args<>(StringListOptions.class).parse("-g", "this", "is");
        assertArrayEquals(options.names(), new String[] {"this", "is"});
    }

    record StringListOptions(@Option("g") String[] names) {
    }

    @Test
    public void should_support_int_list_options() {
        IntListOptions options = new Args<>(IntListOptions.class).parse("-g", "1", "2");
        assertArrayEquals(options.flags(), new Integer[]{1, 2});
    }

    @Test
    public void should_support_negative_int_list_options() {
        IntListOptions options = new Args<>(IntListOptions.class).parse("-g", "-1", "-2");
        assertArrayEquals(options.flags(), new Integer[]{-1, -2});
    }

    record IntListOptions(@Option("g") Integer[] flags) {
    }

}
