import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Args {

    public static Map<String, String[]> toMap(List<String> arguments) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        String regex = "^--?([a-zA-Z]+)$";
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
        String optionShortName = option.value();
        String optionFullName = option.fullName();
        String optionNameMapKey = optionShortName.isEmpty() ? optionFullName : optionShortName;
        String[] optionValues = getValues(argsMap, optionShortName, optionFullName);
        Class<?> optionType = parameter.getType();
        return Optional.ofNullable(PARSERS.get(optionType))
                .map(parser -> parser.parse(optionNameMapKey, optionValues))
                .orElseThrow(UnsupportedOperationException::new);
    }

    private static String[] getValues(Map<String, String[]> argsMap, String optionShortName, String optionFullName) {
        List<String> shortNameValues = Optional.ofNullable(argsMap.get(optionShortName))
                .map(Arrays::asList)
                .orElse(null);
        List<String> fullNameValues = Optional.ofNullable(argsMap.get(optionFullName))
                .map(Arrays::asList)
                .orElse(null);
        if (shortNameValues != null && fullNameValues != null) {
            shortNameValues.addAll(fullNameValues);
            return shortNameValues.toArray(String[]::new);
        } else if (shortNameValues != null) {
            return shortNameValues.toArray(String[]::new);
        } else if (fullNameValues != null) {
            return fullNameValues.toArray(String[]::new);
        }
        return null;
    }

    private static final Map<Class<?>, OptionParser<?>> PARSERS = Map.of(
            int.class, OptionParser.unary(Integer::parseInt, 0),
            String.class, OptionParser.unary(String::valueOf, ""),
            boolean.class, OptionParser.bool(),
            String[].class, OptionParser.list(String::valueOf, String[]::new),
            Integer[].class, OptionParser.list(Integer::parseInt, Integer[]::new)
    );


}
