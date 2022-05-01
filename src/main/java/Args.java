import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Args {

    public static Map<String, String[]> toMap(List<String> arguments) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        String regex = "^-([a-zA-Z-]+)$";
        Pattern pattern = Pattern.compile(regex);
        String optionName = "";
        for (String argument : arguments) {
            Matcher matcher = pattern.matcher(argument);
            if (matcher.matches()) {
                optionName = matcher.group(1);
                hashMap.put(optionName, new ArrayList<>());
            } else if (!optionName.isEmpty()) {
                List<String> values = hashMap.get(optionName);
                values.add(argument);
                hashMap.put(optionName, values);
            }
        }
        return hashMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toArray(String[]::new)));
    }

    public static <T> T parse(Class<T> optionClass, String[] args) {
        Map<String, String[]> argsMap = toMap(List.of(args));
        Constructor<?> constructor = optionClass.getDeclaredConstructors()[0];
        Parameter[] parameters = constructor.getParameters();
        Object[] params = Arrays.stream(parameters).map(parameter -> parseValue(argsMap, parameter)).toArray();
        try {
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static Object parseValue(Map<String, String[]> argsMap, Parameter parameter) {
        Option option = parameter.getAnnotation(Option.class);
        String optionName = option.value();
        String[] optionValues = argsMap.get(optionName);
        Class<?> optionType = parameter.getType();
        return Optional.ofNullable(PARSERS.get(optionType))
                .map(parser -> parser.parse(optionName, optionValues))
                .orElseThrow(UnsupportedOperationException::new);
    }

    private static final Map<Class<?>, OptionParser<?>> PARSERS = Map.of(
            int.class, new SingleValueOptionParser<>(Integer::parseInt, 0),
            String.class, new SingleValueOptionParser<>(String::valueOf, ""),
            boolean.class, new BoolOptionParser(),
            String[].class, new MultiValueOptionParser<>(String::valueOf, String[]::new),
            Integer[].class, new MultiValueOptionParser<>(Integer::parseInt, Integer[]::new)
    );


}
