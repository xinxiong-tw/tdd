import java.util.Optional;

public class BoolOptionParser implements OptionParser<Boolean> {
    @Override
    public Boolean parse(String optionName, String[] optionValues) {
        return Optional.ofNullable(optionValues)
                .map(values -> OptionParser.checkCount(optionName, values, 0))
                .isPresent();
    }
}
