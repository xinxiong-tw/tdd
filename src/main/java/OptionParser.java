import java.util.List;

public interface OptionParser<T> {
    static boolean isValue(List<String> args, int valueIndex) {
        return args.get(valueIndex).startsWith("-");
    }

    T parse(List<String> arguments, String optionName);
}
