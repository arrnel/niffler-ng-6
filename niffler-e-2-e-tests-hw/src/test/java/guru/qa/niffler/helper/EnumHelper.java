package guru.qa.niffler.helper;

import java.lang.reflect.Field;
import java.util.*;

public class EnumHelper {

    public static <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        return enumConstants[new Random().nextInt(enumConstants.length)];
    }

    public static <T extends Enum<?>> Optional<T> getRandomEnum(Class<T> enumClass, List<T> excludeEnums) {
        List<T> enums = Arrays.asList(enumClass.getEnumConstants());
        Collections.shuffle(enums);
        return enums.stream()
                .filter(enumVal -> !excludeEnums.equals(enumVal))
                .findFirst();
    }

    public static <T extends Enum<?>> T getEnumByNameIgnoreCase(Class<T> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public static <T extends Enum<?>> T getEnumByName(Class<T> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equals(name)).findAny().orElse(null);
    }


}
