package gg.rohan.narwhal.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class Storage {

    private static final String PREF_FILE_NAME = "NarwhalPrefFile";

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setupDefaults(Context context) {
        Map<String, String> defaultValues = new HashMap<>();
        defaultValues.put("ip_address", "64.227.174.34");
        defaultValues.put("port", "8080");
        defaultValues.forEach((key, value) -> {
            if (getString(context, key, null) == null) saveString(context, key, value);
        });
    }

}

