import java.util.Optional;

public class BoolOptionParser implements OptionParser<Boolean> {
    @Override
    public Boolean parse(String optionName, String[] optionValues) {
        return Optional.ofNullable(optionValues)
                .map(values -> checkCount(optionName, values))
                .isPresent();
    }

    private String[] checkCount(String optionName, String[] values) {
        if (values.length != 0) {
            throw new TooManyArgumentsException(optionName);
        }
        return values;
    }
}
