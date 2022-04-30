public class BoolOptionParser implements OptionParser<Boolean> {
    @Override
    public Boolean parse(String optionName, String[] optionValues) {
        if (optionValues.length != 0) {
            throw new TooManyArgumentsException(optionName);
        }
        return true;
    }
}
