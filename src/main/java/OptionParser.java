public interface OptionParser<T> {
    static String[] checkCount(String optionName, String[] optionValues, int count) {
        if (optionValues.length < count) {
            throw new TooLessArgumentException(optionName);
        }
        if (optionValues.length > count) {
            throw new TooManyArgumentsException(optionName);
        }
        return optionValues;
    }

    T parse(String optionName, String[] optionValues);
}
