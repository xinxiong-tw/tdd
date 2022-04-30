public interface OptionParser<T> {
    T parse(String optionName, String[] optionValues);
}
