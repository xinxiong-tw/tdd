import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleValueOptionParserTest {
    // single int
    @Test
    public void should_return_8080_while_give_port() {
        IntOption option = Args.parse(IntOption.class, "-p", "8080");
        assertEquals(option.port(), 8080);
    }

    @Test
    public void should_throw_error_while_no_value() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(IntOption.class, "-p"));
    }

    @Test
    public void should_throw_error_while_no_value_2() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(IntOption.class, "-p", "-l"));
    }

    @Test
    public void should_throw_error_while_extra_value() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(IntOption.class, "-p", "8080", "8888"));
    }

    record IntOption(@Option("p") int port) {
    }

    // single string
    @Test
    public void should_return_log_file_path_while_give_directory() {
        StringOption option = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals(option.directory(), "/usr/logs");
    }

    @Test
    public void should_throw_error_while_no_value_for_directory() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(StringOption.class, "-d", "-p", "8080"));
    }

    @Test
    public void should_throw_error_while_no_value_for_directory_2() {
        assertThrows(IllegalArgumentException.class, () -> Args.parse(StringOption.class, "-d", "-l"));
    }

    record StringOption(@Option("d") String directory) {
    }
}