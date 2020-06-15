package id.co.picklon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SP {
    public static final String TK = "token";
    public static final String MOBILE = "mobile";
    public static final String PASSWORD = "password";
    public static final String DEFAULT_ADDRESS = "default_address";
    public static final String LAST_USAGE_TIME = "last_usage_time";

    public static void putLong(Context context, String key, long value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key, 0);
    }
}
