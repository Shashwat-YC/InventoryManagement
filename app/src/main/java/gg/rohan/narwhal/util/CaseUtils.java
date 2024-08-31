package gg.rohan.narwhal.util;

public class CaseUtils {

    public static String normal(String input) {
        if (input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase().concat(input.substring(1).toLowerCase());
    }

    public static String upper(String input) {
        return input.toUpperCase();
    }

}
