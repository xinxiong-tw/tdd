import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs

    @Nested()
    class toMapTest {
        @Test
        public void should_return_map_after_parse_arguments() {

            Map<String, String[]> map = Args.toMap(List.of("-p", "8080", "-l", "-d", "/usr/log"));

            assertArrayEquals(map.get("-p"), new String[] {"8080"});
            assertArrayEquals(map.get("-l"), new String[] {});
            assertArrayEquals(map.get("-d"), new String[] {"/usr/log"});
        }

        @Test
        public void should_return_map_with_list_option_after_parse_list_arguments() {
            Map<String, String[]> map = Args.toMap(List.of("-g", "hello", "world"));

            assertArrayEquals(map.get("-g"), new String[] {"hello", "world"});
        }

        // --port 8080
        @Test
        public void should_return_map_with_full_name_option_after_parse_list_arguments() {
            Map<String, String[]> map = Args.toMap(List.of("--port", "8080"));
            assertArrayEquals(map.get("--port"), new String[] {"8080"});
        }

        @Test
        public void should_support_multi_same_name_option() {
            Map<String, String[]> map = Args.toMap(List.of("-e", "MYSQL_ALLOW_EMPTY_PASSWORD=yes", "-e", "MYSQL_DATABASE=test"));
            assertArrayEquals(map.get("-e"), new String[] {"MYSQL_ALLOW_EMPTY_PASSWORD=yes", "MYSQL_DATABASE=test"});
        }
    }


    @Test
    public void should_return_true_after_parse_bool_option() {
        BoolOption option = Args.parse(BoolOption.class, new String[]{"-l"});
        assertTrue(Objects.requireNonNull(option).logging());
    }

    @Test
    public void should_throw_error_if_too_many_arguments() {
        TooManyArgumentsException tooManyArgumentsException = assertThrows(TooManyArgumentsException.class, () -> Args.parse(BoolOption.class, new String[]{"-l", "p"}));
        assertEquals(tooManyArgumentsException.argument, "l");
    }

    @Test
    public void should_return_false_if_option_not_present() {
        BoolOption option = Args.parse(BoolOption.class, new String[]{""});
        assertFalse(Objects.requireNonNull(option).logging());
    }

    record BoolOption(@Option("l") boolean logging) {
    }

    @Test
    public void should_return_8080_after_parse_int_option() {
        IntOption option = Args.parse(IntOption.class, new String[]{"-p", "8080"});
        assertEquals(Objects.requireNonNull(option).port(), 8080);
    }

    @Test
    public void should_throw_too_many_arguments_if_option_has_more_than_one_value() {
        TooManyArgumentsException tooManyArgumentsException =
                assertThrows(TooManyArgumentsException.class,
                        () -> Args.parse(IntOption.class, new String[]{"-p", "8080", "9999"}));
        assertEquals(tooManyArgumentsException.argument, "p");
    }

    @Test
    public void should_throw_too_less_arguments_if_option_has_no_value() {
        TooLessArgumentException tooLessArgumentException =
                assertThrows(TooLessArgumentException.class,
                        () -> Args.parse(IntOption.class, new String[]{"-p"}));
        assertEquals(tooLessArgumentException.argument, "p");
    }

    @Test
    public void should_return_default_in_value_if_int_option_is_not_present() {
        IntOption option = Args.parse(IntOption.class, new String[]{});
        assertEquals(Objects.requireNonNull(option).port(), 0);
    }

    record IntOption(@Option("p") int port) {
    }

    @Test
    public void should_return_string_after_parse_string_option() {
        StringOption option = Args.parse(StringOption.class, new String[]{"-d", "/usr/log"});
        assertEquals(Objects.requireNonNull(option).directory(), "/usr/log");
    }

    @Test
    public void should_throw_too_many_arguments_if_option_has_more_than_one_string_values() {
        TooManyArgumentsException tooManyArgumentsException =
                assertThrows(TooManyArgumentsException.class,
                        () -> Args.parse(StringOption.class, new String[]{"-d", "/usr/log", "/var/log"}));
        assertEquals(tooManyArgumentsException.argument, "d");
    }

    @Test
    public void should_throw_too_less_arguments_if_option_has_not_enough_values() {
        TooLessArgumentException tooLessArgumentException =
                assertThrows(TooLessArgumentException.class,
                        () -> Args.parse(StringOption.class, new String[]{"-d"}));
        assertEquals(tooLessArgumentException.argument, "d");
    }

    @Test
    public void should_return_default_in_value_if_string_option_is_not_present() {
        StringOption option = Args.parse(StringOption.class, new String[]{});
        assertEquals(Objects.requireNonNull(option).directory(), "");
    }

    record StringOption(@Option("d") String directory) {
    }

    @Test
    public void should_return_multiple_options_after_parse_arguments() {
        Options option = Args.parse(Options.class, new String[]{"-d", "/usr/log", "-p", "8080", "-l"});
        assertEquals(Objects.requireNonNull(option).directory(), "/usr/log");
        assertTrue(option.logging());
        assertEquals(option.port(), 8080);
    }
    record Options(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    @Test
    public void should_return_list_string_values_after_parse_list_strings() {
        ListOption option = Args.parse(ListOption.class, new String[]{"-g", "this", "is", "a", "list"});
        assertArrayEquals(new String[]{"this", "is", "a", "list"}, Objects.requireNonNull(option).groups());
    }

    record ListOption(@Option("g") String[] groups) {
    }

    @Test
    public void should_return_int_list_values_after_parse_int_list() {
        IntListOption option = Args.parse(IntListOption.class, new String[]{"-g", "-1", "0", "1", "10"});
        assertArrayEquals(new Integer[]{-1, 0, 1, 10}, Objects.requireNonNull(option).numbers());
    }

    record IntListOption(@Option("g") Integer[] numbers) {
    }

    @Test
    public void should_support_full_name_of_option() {
        FullNameOption option = Args.parse(FullNameOption.class, new String[]{"--port", "8080"});
        assertEquals(8080, Objects.requireNonNull(option).port());
    }

    // -p 8080
    @Test
    public void should_distinguish_full_name_and_short_name() {
        FullNameOption option = Args.parse(FullNameOption.class, new String[]{"-port", "8080"});
        assertEquals(0, Objects.requireNonNull(option).port());
    }

    record FullNameOption(@Option(value = "p", fullName = "port") int port) {
    }

    @Test
    public void should_join_full_name_values_with_short_name_values_after_parse() {
        FullNameListOption option = Args.parse(FullNameListOption.class, new String[]{"-n", "1", "2", "--numbers", "3"});
        assertArrayEquals(new Integer[]{1, 2, 3}, Objects.requireNonNull(option).numbers());
    }

    record FullNameListOption(@Option(value = "n", fullName = "numbers") Integer[] numbers) {
    }

    @Test
    public void should_parse_map_options() {
        MapOption option = Args.parse(MapOption.class, new String[]{"-e", "MYSQL_ALLOW_EMPTY_PASSWORD=yes", "-e", "MYSQL_DATABASE=test"});
        assertEquals(Map.of("MYSQL_ALLOW_EMPTY_PASSWORD", "yes", "MYSQL_DATABASE", "test"), Objects.requireNonNull(option).settings());
    }

    @Test
    public void should_return_empty_map_options() {
        MapOption option = Args.parse(MapOption.class, new String[]{});
        assertEquals(Map.of(), Objects.requireNonNull(option).settings());
    }

    @Test
    public void should_throw_error_with_no_value() {
        TooLessArgumentException tooLessArgumentException = assertThrows(TooLessArgumentException.class, () -> Args.parse(MapOption.class, new String[]{"-e"}));
        assertEquals(tooLessArgumentException.argument, "e");
    }

    record MapOption(@Option("e") Map<String, String> settings) {
    }

    @Nested
    class HelpMessageTest {

        private ByteArrayOutputStream outputStreamCaptor;
        private final PrintStream systemOut = System.out;

        @BeforeEach
        void setUp() {
            outputStreamCaptor = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStreamCaptor));
        }

        @AfterEach()
        void tearDown() {
            System.setOut(systemOut);
        }

        @Test
        public void should_show_default_help_message() {
            Args.parse(MapOption.class, new String[]{"-h"});
            assertEquals("-e settings\n", outputStreamCaptor.toString());
        }

        @Test
        public void should_show_full_name_with_default_help_message() {
            Args.parse(FullNameOption.class, new String[]{"-h"});
            assertEquals("-p --port port\n", outputStreamCaptor.toString());
        }

        @Test
        public void should_show_only_full_name_with_default_help_message() {
            Args.parse(OnlyFullNameOption.class, new String[]{"-h"});
            assertEquals("--port port\n", outputStreamCaptor.toString());
        }

        record OnlyFullNameOption(@Option(fullName = "port", value = "") int port) {}

        @Test
        public void should_show_description() {
            Args.parse(OptionWithDescription.class, new String[]{"-h"});
            assertEquals("-p port to use\n", outputStreamCaptor.toString());
        }

        record OptionWithDescription(@Option(value = "p", description = "port to use") int port) {
        }

        @Test
        public void should_show_short_name_and_fullName_and_description() {
            Args.parse(AllOption.class, new String[]{"-h"});
            assertEquals("-p --port port to use\n", outputStreamCaptor.toString());
        }

        record AllOption(@Option(value = "p", fullName = "port", description = "port to use") int port) {}

        @Test
        public void should_show_multiple_option_help_message() {
            Args.parse(MultipleAllOptions.class, new String[]{"-h"});
            assertEquals("""
                    -p --port port to use
                    -d --directory log file's directory
                    """, outputStreamCaptor.toString());
        }

        record MultipleAllOptions(
                @Option(value = "p", fullName = "port", description = "port to use") int port,
                @Option(value = "d", fullName = "directory", description = "log file's directory") String directory
        ) {
        }

        @Test
        public void should_ignore_other_option_if_contains_help_command() {
            MultipleAllOptions option = Args.parse(MultipleAllOptions.class, new String[]{"-p", "8080", "-h"});
            assertEquals("""
                    -p --port port to use
                    -d --directory log file's directory
                    """, outputStreamCaptor.toString());
            assertNull(option);
        }
    }

}
