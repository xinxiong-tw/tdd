import java.util.List;

public interface OptionParser<T> {
    static boolean isValueAt(List<String> args, int valueIndex) {
        String arg = args.get(valueIndex);
        return isOption(arg);
    }

    static boolean isOption(String arg) {
        return arg.startsWith("-");
    }

    T parse(List<String> arguments, String optionName);
}
