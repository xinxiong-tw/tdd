import java.util.List;

class BoolOptionParser implements OptionParser<Boolean> {
    public Boolean parse(List<String> arguments, String optionName) {
        int optionIndex = arguments.indexOf(optionName);
        if (optionIndex == -1) {
            return false;
        }
        OptionParser.getAndCheckValueCount(arguments, optionName, 0);
        return true;
    }
}
