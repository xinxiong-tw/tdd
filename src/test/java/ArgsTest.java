import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

}
