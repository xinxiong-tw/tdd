import java.util.List;

class BoolOptionParser implements OptionParser<Boolean> {
    public Boolean parse(List<String> arguments, String optionName) {
        return OptionParser.getAndCheckValueCount(arguments, optionName, 0).isPresent();
    }
}
