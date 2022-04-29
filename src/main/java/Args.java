import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Args {

    public static Map<String, String[]> parse(List<String> arguments) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        final String[] optionName = new String[1];
        arguments.forEach(argument -> {
            String regex = "^-([a-zA-Z-]+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(argument);
            if (matcher.matches()) {
                optionName[0] = matcher.group(1);
                hashMap.put(optionName[0], new ArrayList<>());
            } else if (!optionName[0].isEmpty()) {
                List<String> values = hashMap.get(optionName[0]);
                values.add(argument);
                hashMap.put(optionName[0], values);
            }
        });
        return hashMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toArray(String[]::new)));
    }

}
