package gg.rohan.narwhal.util;

public class EnumUtils {

    public static <T extends Enum<T>> T fromString(Class<T> enumType, String string) {
        return Enum.valueOf(enumType, string.toUpperCase());
    }

}
