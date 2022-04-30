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

    record BoolOption(@Option("l") boolean logging) {
    }

    @Test
    public void should_return_8080_after_parse_int_option() {
        IntOption option = Args.parse(IntOption.class, new String[]{"-p", "8080"});
        assertEquals(option.port(), 8080);
    }

    record IntOption(@Option("p") int port) {
    }

    record Options(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

}
