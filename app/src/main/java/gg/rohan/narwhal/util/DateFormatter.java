package gg.rohan.narwhal.util;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateFormatter {

    public static String formatDate(TemporalAccessor accessor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(accessor);
    }

}
