import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    @Test
    public void should_parse_multiple_options_one_time() {
        LogOptions options = Args.parse(LogOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.logging());
        assertEquals(options.port(), 8080);
        assertEquals(options.directory, "/usr/logs");
    }

    record LogOptions(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    @Test
    public void should_throw_error_while_no_option_annotation() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(NoOptionOptions.class, "-l"));
    }

    record NoOptionOptions(boolean logging) {
    }


    @Test
    public void should_support_string_list_options() {
        StringListOptions options = Args.parse(StringListOptions.class, "-g", "this", "is");
        assertArrayEquals(options.names(), new String[] {"this", "is"});
    }

    record StringListOptions(@Option("g") String[] names) {
    }

    @Test
    public void should_support_int_list_options() {
        IntListOptions options = Args.parse(IntListOptions.class, "-g", "1", "2");
        assertArrayEquals(options.flags(), new Integer[]{1, 2});
    }

    @Test
    public void should_support_negative_int_list_options() {
        IntListOptions options = Args.parse(IntListOptions.class, "-g", "-1", "-2");
        assertArrayEquals(options.flags(), new Integer[]{-1, -2});
    }

    record IntListOptions(@Option("g") Integer[] flags) {
    }

}
