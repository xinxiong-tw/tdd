import java.util.List;

public interface OptionParser<T> {
    T parse(List<String> arguments, String optionName);
}
