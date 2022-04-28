import java.util.List;

class BoolOptionParser implements OptionParser<Boolean> {
    public Boolean parse(List<String> arguments, String optionName) {
        return arguments.contains(optionName);
    }
}
