import java.util.List;

class BoolOptionParser implements OptionParser<Boolean> {
    public Boolean parse(List<String> arguments, String optionName) {
        int optionIndex = arguments.indexOf(optionName);
        if (optionIndex == -1) {
            return false;
        }
        if (optionIndex + 1 < arguments.size() && !OptionParser.isValue(arguments, optionIndex + 1)) {
            throw new IllegalArgumentException("expect no value after bool option " + optionName);
        }
        return true;
    }
}
