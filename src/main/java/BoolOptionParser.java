import java.util.List;

class BoolOptionParser implements OptionParser<Boolean> {
    public Boolean parse(List<String> arguments, String optionName) {
        int optionIndex = arguments.indexOf(optionName);
        if (optionIndex == -1) {
            return false;
        }
        List<String> optionRawValues = OptionParser.getOptionRawValues(arguments, optionIndex + 1);
        if (!optionRawValues.isEmpty()) {
            throw new IllegalArgumentException("expect no value after bool option " + optionName);
        }
        return true;
    }
}
