import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Args {

    public static Map<String, String[]> toMap(List<String> arguments) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        String regex = "^(--?[a-zA-Z]+)$";
        Pattern pattern = Pattern.compile(regex);
        String optionName = "";
        for (String argument : arguments) {
            Matcher matcher = pattern.matcher(argument);
            if (matcher.matches()) {
                optionName = matcher.group(1);
                if (!hashMap.containsKey(optionName)) {
                    hashMap.put(optionName, new ArrayList<>());
                }
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
        if (showHelpMessageIfNeeded(argsMap, parameters)) return null;
        Object[] params = Arrays.stream(parameters).map(parameter -> parseValue(argsMap, parameter)).toArray();
        try {
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static boolean showHelpMessageIfNeeded(Map<String, String[]> argsMap, Parameter[] parameters) {
        if (argsMap.get("-h") != null) {
            StringBuilder helpMessage = new StringBuilder();
            Arrays.stream(parameters).forEach(parameter -> {
                Option option = parameter.getAnnotation(Option.class);
                String optionName = option.value();
                if (!optionName.isEmpty()) {
                    helpMessage.append("-").append(optionName);
                }
                String fullOptionName = option.fullName();
                if (!fullOptionName.isEmpty()) {
                    if (!helpMessage.isEmpty()) {
                        helpMessage.append(" ");
                    }
                    helpMessage.append("--").append(fullOptionName);
                }
                helpMessage.append(" ").append(parameter.getName());
            });
            System.out.println(helpMessage);
            return true;
        }
        return false;
    }

    private static Object parseValue(Map<String, String[]> argsMap, Parameter parameter) {
        Option option = parameter.getAnnotation(Option.class);
        String optionShortName = option.value();
        String optionFullName = option.fullName();
        String optionNameMapKey = optionShortName.isEmpty() ? optionFullName : optionShortName;
        String[] optionValues = getValues(argsMap, new String[]{"-" + optionShortName, "--" + optionFullName});
        Class<?> optionType = parameter.getType();
        return Optional.ofNullable(PARSERS.get(optionType))
                .map(parser -> parser.parse(optionNameMapKey, optionValues))
                .orElseThrow(UnsupportedOperationException::new);
    }

    private static String[] getValues(Map<String, String[]> argsMap, String[] optionNames) {
        List<String> values = null;
        for (String optionName : optionNames) {
            List<String> optionValues = Optional.ofNullable(argsMap.get(optionName))
                    .map(Arrays::asList)
                    .orElse(null);
            if (optionValues == null) {
                continue;
            }
            if (values == null) {
                values = new ArrayList<>(optionValues);
            } else {
                values.addAll(optionValues);
            }
        }
        return Optional.ofNullable(values).map(it -> it.toArray(String[]::new)).orElse(null);
    }

    private static final Map<Class<?>, OptionParser<?>> PARSERS = Map.of(
            int.class, OptionParser.unary(Integer::parseInt, 0),
            String.class, OptionParser.unary(String::valueOf, ""),
            boolean.class, OptionParser.bool(),
            String[].class, OptionParser.list(String::valueOf, String[]::new),
            Integer[].class, OptionParser.list(Integer::parseInt, Integer[]::new),
            Map.class, OptionParser.map()
    );


}
