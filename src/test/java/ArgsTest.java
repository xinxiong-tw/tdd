import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    @Test
    public void should_return_map_after_parse_arguments() {

        Map<String, String[]> map = Args.parse(List.of("-p", "8080", "-l", "-d", "/usr/log"));

        assertArrayEquals(map.get("p"), new String[] {"8080"});
        assertArrayEquals(map.get("l"), new String[] {});
        assertArrayEquals(map.get("d"), new String[] {"/usr/log"});
    }

    @Test
    public void should_return_map_with_list_option_after_parse_list_arguments() {
        Map<String, String[]> map = Args.parse(List.of("-g", "hello", "world"));

        assertArrayEquals(map.get("g"), new String[] {"hello", "world"});
    }
}
