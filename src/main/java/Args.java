import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.rmi.UnexpectedException;
import java.util.*;
import java.util.function.IntFunction;
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
        Object[] params = Arrays.stream(parameters).map(parameter -> {
            Option option = parameter.getAnnotation(Option.class);
            String optionName = option.value();
            String[] optionValues = argsMap.get(optionName);
            Class<?> optionType = parameter.getType();
            if (optionType == int.class) {
                return Integer.parseInt(optionValues[0]);
            }
            if (optionType == String.class) {
                return optionValues[0];
            }
            return optionValues != null;
        }).toArray();
        try {
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
