public class BoolOptionParser implements OptionParser<Boolean> {
    @Override
    public Boolean parse(String[] optionValues) {
        return optionValues != null;
    }
}
